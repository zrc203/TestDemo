package com.zrc.ldap.service;

/*
 * Copyright (c) 2005 MedImpact Healthcare Systems, Inc.
 * All Rights Reserved.
 *
 * History
 *
 * $Log:
 *  1    MedResponse 1.0         1/15/13 3:17:55 PM PST Harry Smith     Phase
 *       3, PB 6 updates
 * $
 */



import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;


/**
 * This class provides functions for integration to LDAP.
 *
 * @author Harry Smith
 */
public class LDAPService {
    // Constants
    private static final ArrayList backupServerURLs =
        LDAPConfig.getInstance().getPropertyList("LDAP.BackupServerUrls", ";");
    private static final String CONTEXT_FACTORY = LDAPConfig.getProperty("LDAP.ContextFactory");
    private static final String AUTHENTICATE_TYPE = LDAPConfig.getProperty("LDAP.AuthenticationType");

    public LdapContext ctx;
    private boolean useBackupServers = true;

     public LDAPService() {
     
     }

    /*
     * Bind user

     */
    public boolean bindUser(String serverURL, String userURL, String password) throws NamingException, LDAPException
    {

        System.out.println ("Servers " + backupServerURLs);
        boolean success =false;
        try {
            // Since we call bindUser 2 times when we do loginWebUser, we should
            // close the existing context before a new one is created.
            closeContext();

            // setup properties to communicate with LDAP server
            Hashtable<String, String> props = new Hashtable<String, String>();
            props.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
            props.put(Context.PROVIDER_URL, serverURL);

            // establish connection as anonymous user
            ctx = new InitialLdapContext(props, null);

            // store user credentials in environment
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userURL);
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, AUTHENTICATE_TYPE);

            // now reconnect so new user credentials are used
            ctx.reconnect(null);

            success = true;
        }
        // catch more generic exception NamingException since other exceptions
        // such as CommunicationException, PartialResultException (in case of
        // referral), etc. are derived from NamingException
        catch (CommunicationException commEx) {
            System.err.println("Failed to communicate LDAP server " + serverURL);
            success = retryBindUser(userURL, password);
        } catch (PartialResultException prEx) {
            System.err.println("Failed to communicate LDAP server " + serverURL);
            success = retryBindUser(userURL, password);
        }

        return success;
    }

    /*
     * Bind user using back-up servers

     */
    private boolean retryBindUser(String userURL, String password) throws NamingException, LDAPException {
        boolean success = false;

        if (useBackupServers) {
            useBackupServers = false; // to avoid infinite recursion in case of failure of backup server binding
            Iterator itr = backupServerURLs.iterator();
            String serverURL = null;
            while (itr.hasNext()) {
                try {
                    serverURL = (String)itr.next();
                    if (bindUser(serverURL, userURL, password)) {
                        // don't continue if binding to one of backup servers is successful
                        success = true;
                        break;
                    }
                } catch (CommunicationException commEx) {
                    System.err.println("Failed to communicate LDAP server " + serverURL);
                } catch (PartialResultException prEx) {
                    System.err.println("Failed to communicate LDAP server " + serverURL);
                }
            }
            useBackupServers = true; // restore the original value
        }

        if (!success)
            throw new UnableToBindUserException();

        return success;
    }

    /*
     * Bind application admin user
     */
    public void bindAdminUser(boolean isReadOnly) throws NamingException, LDAPException {
        String serverURL = LDAPConfig.getProperty("LDAP.MasterServerUrl");
        if (isReadOnly)
            serverURL = LDAPConfig.getProperty("LDAP.ReadOnlyServerUrl");
        System.out.println ("Master Server " + serverURL);
        String userURL = 
            LDAPConfig.getProperty("LDAP.AdminUserKey") + "=" + LDAPConfig.getProperty("LDAP.AdminUsername") + "," +
            LDAPConfig.getProperty("LDAP.AdminUserContext");

        bindUser(serverURL, userURL, LDAPConfig.getProperty("LDAP.AdminPassword"));
    }


    public String findUserURL(String username) throws NamingException, LDAPException {
        return findUserURL(username, true);
    }

    /*
     * Given user name, this is used to find full path for an user in LDAP.
     */
    public String findUserURL(String username, boolean isReadyOnly) throws NamingException, LDAPException {
        String userURL = null;
        // bind as admin
        bindAdminUser(isReadyOnly);

        SearchControls ctls = new SearchControls();

        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(uid=" + username + ")";
        NamingEnumeration result = ctx.search(LDAPConfig.getProperty("LDAP.UserContext"), filter, ctls);

        // get user path
        // note: expect there is only one result record

        while (result.hasMore()) {
            SearchResult sr = (SearchResult)result.next();
            userURL = sr.getName() + "," + LDAPConfig.getProperty("LDAP.UserContext");
        }
        return userURL;
    }

    /*
     * Given user name, this is used to find full path for an user in LDAP.
     */
    public List findUserRoles(String username, boolean isReadyOnly) throws NamingException, LDAPException {
        List<String> userRoles = new ArrayList<String>();
        String userRole;
        // bind as admin
        bindAdminUser(isReadyOnly);

        SearchControls ctls = new SearchControls();
        String userUrl = this.findUserURL(username);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(|(member=" + userUrl + ")(uniqueMember=" + userUrl + "))";
        //String filter = "(uid=vbobba)";
        NamingEnumeration result = ctx.search("dc=medimpact,dc=com", filter, ctls);

        // get user path
        // note: expect there is only one result record

        while (result.hasMore()) {
            SearchResult sr = (SearchResult)result.next();
            userRole = sr.getName();
            userRoles.add(userRole + ",dc=medimpact,dc=com");
        }

        return userRoles;
    }

    /*
     * Find user context
     */
    public String findUserContext(String username) throws NamingException, LDAPException {
        String userCtx = null;
        // bind as admin
        bindAdminUser(true);

        SearchControls ctls = new SearchControls();

        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(" + LDAPConfig.getProperty("LDAP.UserKey") + "=" + username + ")";
        NamingEnumeration result = ctx.search(LDAPConfig.getProperty("LDAP.UserContext"), filter, ctls);

        // get user path
        // note: expect there is only one result record
        while (result.hasMore()) {
            SearchResult sr = (SearchResult)result.next();
            String name = sr.getName();
            int delim = name.indexOf(",");
            userCtx = name.substring(delim + 1, name.length()) + "," + LDAPConfig.getProperty("LDAP.UserContext");
        }

        return userCtx;
    }

    public ArrayList findUserGroups(String userURL) throws NamingException, LDAPException {
        ArrayList<String> groups = null;
        // bind as admin
        bindAdminUser(true);

        SearchControls ctls = new SearchControls();

        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(cn=*)";
        NamingEnumeration result = ctx.search(LDAPConfig.getProperty("LDAP.GroupContext"), filter, ctls);

        // loop thru each group
        while (result.hasMore()) {
            SearchResult sr = (SearchResult)result.next();
            String name = sr.getName();

            Attributes attrs = sr.getAttributes();
            if (attrs != null) {
                // loop thru each attributes for this group
                for (NamingEnumeration attrEnum = attrs.getAll(); attrEnum.hasMore(); ) {
                    Attribute attr = (Attribute)attrEnum.next();
                    if ("uniquemember".equalsIgnoreCase(attr.getID())) {
                        for (NamingEnumeration mbrEnum = attr.getAll(); mbrEnum.hasMore(); ) {
                            String mbr = (String)mbrEnum.next();
                            if (userURL.equalsIgnoreCase(mbr)) {
                                if (groups == null)
                                    groups = new ArrayList<String>();

                                int delim = name.indexOf("=");
                                groups.add(name.substring(delim + 1, name.length()));
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        return groups;
    }

    public void verifyUserCredentials(String username, String password) throws Exception
    {
        bindAdminUser(true);
        
         // search for this user and get user URL (dn)
         String[] attrIDs = { "uid" };
         SearchControls ctls = new SearchControls();
         ctls.setReturningAttributes(attrIDs);
         ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
         String filter = "(uid=" + username + ")";
         NamingEnumeration result = ctx.search(LDAPConfig.getProperty("LDAP.UserContext"), filter, ctls);

         // get user path and login this user
         // note: expect there is only one result record
         String userURL = null;
         while (result.hasMore()) {
             SearchResult sr = (SearchResult)result.next();
             userURL = sr.getName() + "," + LDAPConfig.getProperty("LDAP.UserContext");
         }
         if (userURL != null) {
             String serverURL = LDAPConfig.getProperty("LDAP.ReadOnlyServerUrl");
             bindUser(serverURL, userURL, password);
         } else {
             closeContext();
             throw new AuthenticationException(LDAPConfig.getProperty("error.InvalidUsername"));
         }

         // not use context anymore, close it
         closeContext();
    }


    /* ===================================
 *  Private Functions
 * =================================== */



    /**
     * close context
     */
    private void closeContext() {
        try {
            if (ctx != null)
                ctx.close();
        } catch (NamingException nEx) {
            System.err.println("Failed to close LDAP context.");
        }
    }





    // ========================================================================





}  // end of class
