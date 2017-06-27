package com.linbit.drbdmanage.dbdrivers.derby;

public class DerbyConstants
{
    // View names
    public static final String VIEW_SEC_IDENTITIES_LOAD = "SEC_IDENTITIES_LOAD";
    public static final String VIEW_SEC_ROLES_LOAD      = "SEC_ROLES_LOAD";
    public static final String VIEW_SEC_TYPES_LOAD      = "SEC_TYPES_LOAD";
    public static final String VIEW_SEC_TYPE_RULES_LOAD = "SEC_TYPE_RULES_LOAD";

    // Table names
    public static final String TBL_SEC_CONFIGURATION      = "SEC_CONFIGURATION";
    public static final String TBL_SEC_IDENTITIES         = "SEC_IDENTITIES";
    public static final String TBL_SEC_TYPES              = "SEC_TYPES";
    public static final String TBL_SEC_ROLES              = "SEC_ROLES";
    public static final String TBL_SEC_ID_ROLE_MAP        = "SEC_ID_ROLE_MAP";
    public static final String TBL_SEC_ACCESS_TYPES       = "SEC_ACCESS_TYPES";
    public static final String TBL_SEC_TYPE_RULES         = "SEC_TYPE_RULES";
    public static final String TBL_SEC_DFLT_ROLES         = "SEC_DFLT_ROLES";
    public static final String TBL_SEC_OBJECT_PROTECTION  = "SEC_OBJECT_PROTECTION";
    public static final String TBL_SEC_ACL_MAP            = "SEC_ACL_MAP";
    public static final String TBL_CTRL_CONFIGURATION     = "CTRL_CONFIGURATION";
    public static final String TBL_NODES                  = "NODES";
    public static final String TBL_NODE_NET_INTERFACES    = "NODE_NET_INTERFACES";
    public static final String TBL_RESOURCE_DEFINITIONS   = "RESOURCE_DEFINITIONS";
    public static final String TBL_NODE_RESOURCE          = "NODE_RESOURCE";
    public static final String TBL_VOLUME_DEFINITIONS     = "VOLUME_DEFINITIONS";
    public static final String TBL_STOR_POOL_DEFINITIONS  = "STOR_POOL_DEFINITIONS";
    public static final String TBL_NODE_STOR_POOL         = "NODE_STOR_POOL";
    public static final String TBL_CONNECTION_DEFINITIONS = "CONNECTION_DEFINITIONS";
    public static final String TBL_PROPS_CONTAINERS       = "PROPS_CONTAINERS";

    // SEC_CONFIGURATION column names 

    public static final String ENTRY_KEY     = "ENTRY_KEY";
    public static final String ENTRY_DSP_KEY = "ENTRY_DSP_KEY";
    public static final String ENTRY_VALUE   = "ENTRY_VALUE";

    // SEC_IDENTITIES column names 

    public static final String IDENTITY_NAME     = "IDENTITY_NAME";
    public static final String IDENTITY_DSP_NAME = "IDENTITY_DSP_NAME";
    public static final String PASS_SALT         = "PASS_SALT";
    public static final String PASS_HASH         = "PASS_HASH";
    public static final String ID_ENABLED        = "ID_ENABLED";
    public static final String ID_LOCKED         = "ID_LOCKED";

    // SEC_TYPES column names 

    public static final String TYPE_NAME     = "TYPE_NAME";
    public static final String TYPE_DSP_NAME = "TYPE_DSP_NAME";
    public static final String TYPE_ENABLED  = "TYPE_ENABLED";

    // SEC_ROLES column names 

    public static final String ROLE_NAME       = "ROLE_NAME";
    public static final String ROLE_DSP_NAME   = "ROLE_DSP_NAME";
    public static final String DOMAIN_NAME     = "DOMAIN_NAME";
    public static final String ROLE_ENABLED    = "ROLE_ENABLED";
    public static final String ROLE_PRIVILEGES = "ROLE_PRIVILEGES";

    // SEC_ACCESS_TYPES column names 

    public static final String ACCESS_TYPE_NAME  = "ACCESS_TYPE_NAME";
    public static final String ACCESS_TYPE_VALUE = "ACCESS_TYPE_VALUE";

    // SEC_TYPE_RULES column names 

    public static final String ACCESS_TYPE = "ACCESS_TYPE";

    // SEC_OBJECT_PROTECTION column names 

    public static final String OBJECT_PATH           = "OBJECT_PATH";
    public static final String CREATOR_IDENTITY_NAME = "CREATOR_IDENTITY_NAME";
    public static final String OWNER_ROLE_NAME       = "OWNER_ROLE_NAME";
    public static final String SECURITY_TYPE_NAME    = "SECURITY_TYPE_NAME";

    // NODES column names 

    public static final String NODE_NAME     = "NODE_NAME";
    public static final String NODE_DSP_NAME = "NODE_DSP_NAME";
    public static final String NODE_FLAGS    = "NODE_FLAGS";
    public static final String NODE_TYPE     = "NODE_TYPE";

    // NODE_NET_INTERFACES column names 

    public static final String UUID                = "UUID";
    public static final String NODE_NET_NAME       = "NODE_NET_NAME";
    public static final String NODE_NET_DSP_NAME   = "NODE_NET_DSP_NAME";
    public static final String INET_ADDRESS        = "INET_ADDRESS";
    public static final String INET_TRANSPORT_TYPE = "INET_TRANSPORT_TYPE";

    // RESOURCE_DEFINITIONS column names 

    public static final String RESOURCE_NAME     = "RESOURCE_NAME";
    public static final String RESOURCE_DSP_NAME = "RESOURCE_DSP_NAME";

    // VOLUME_DEFINITIONS column names 

    public static final String VLM_ID        = "VLM_ID";
    public static final String VLM_SIZE      = "VLM_SIZE";
    public static final String VLM_MINOR_NR  = "VLM_MINOR_NR";

    // STOR_POOL_DEFINITIONS column names 

    public static final String POOL_NAME     = "POOL_NAME";
    public static final String POOL_DSP_NAME = "POOL_DSP_NAME";

    // NODE_STOR_POOL column names 

    public static final String DRIVER_NAME = "DRIVER_NAME";

    // PROPS_CONTAINERS column names 

    public static final String PROPS_INSTANCE = "PROPS_INSTANCE";
    public static final String PROP_KEY       = "PROP_KEY";
    public static final String PROP_VALUE     = "PROP_VALUE";
}
