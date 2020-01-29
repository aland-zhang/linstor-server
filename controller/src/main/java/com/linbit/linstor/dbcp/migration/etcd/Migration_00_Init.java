package com.linbit.linstor.dbcp.migration.etcd;

import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.ResourceGroups;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecAccessTypes;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecAclMap;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecConfiguration;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecDfltRoles;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecIdRoleMap;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecIdentities;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecObjectProtection;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecRoles;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecTypeRules;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.SecTypes;
import com.linbit.linstor.dbdrivers.GeneratedDatabaseTables.StorPoolDefinitions;
import com.linbit.linstor.dbdrivers.etcd.EtcdUtils;
import com.linbit.linstor.transaction.EtcdTransaction;

@SuppressWarnings("checkstyle:typename")
public class Migration_00_Init extends EtcdMigration
{
    private static final String PRIMARY_KEY_DELI = ":";

    private static void secConfiguration(
        EtcdTransaction tx,
        String entryKey,
        String entryDspKey,
        String entryValue
    )
    {
        tx.put(tblKey(SecConfiguration.ENTRY_DSP_KEY, entryKey), entryDspKey);
        tx.put(tblKey(SecConfiguration.ENTRY_VALUE, entryKey), entryValue);
    }

    private static void secIdentities(
        EtcdTransaction tx,
        String identityName,
        String identityDspName,
        boolean idEnabled,
        boolean idLocked
    )
    {
        tx.put(tblKey(SecIdentities.IDENTITY_NAME, identityName), identityName);
        tx.put(tblKey(SecIdentities.IDENTITY_DSP_NAME, identityName), identityDspName);
        tx.put(tblKey(SecIdentities.ID_ENABLED, identityName), Boolean.toString(idEnabled).toUpperCase());
        tx.put(tblKey(SecIdentities.ID_LOCKED, identityName), Boolean.toString(idLocked).toUpperCase());
    }

    private static void secTypes(
        EtcdTransaction tx,
        String typeName,
        String typeDspName,
        boolean typeEnabled
    )
    {
        tx.put(tblKey(SecTypes.TYPE_NAME, typeName), typeName);
        tx.put(tblKey(SecTypes.TYPE_DSP_NAME, typeName), typeDspName);
        tx.put(tblKey(SecTypes.TYPE_ENABLED, typeName), Boolean.toString(typeEnabled).toUpperCase());
    }

    private static void secRoles(
        EtcdTransaction tx,
        String roleName,
        String roleDspName,
        String domainName,
        boolean roleEnabled,
        int rolePrivileges
    )
    {
        tx.put(tblKey(SecRoles.ROLE_NAME, roleName), roleName);
        tx.put(tblKey(SecRoles.ROLE_DSP_NAME, roleName), roleDspName);
        tx.put(tblKey(SecRoles.DOMAIN_NAME, roleName), domainName);
        tx.put(tblKey(SecRoles.ROLE_ENABLED, roleName), Boolean.toString(roleEnabled).toUpperCase());
        tx.put(tblKey(SecRoles.ROLE_PRIVILEGES, roleName), Integer.toString(rolePrivileges));
    }

    private static void secIdRoleMap(
        EtcdTransaction tx,
        String identityName,
        String roleName
    )
    {
        final String pk = identityName + PRIMARY_KEY_DELI + roleName;
        tx.put(tblKey(SecIdRoleMap.IDENTITY_NAME, pk), identityName);
        tx.put(tblKey(SecIdRoleMap.ROLE_NAME, pk), roleName);
    }

    private static void secAccessTypes(
        EtcdTransaction tx,
        String accessTypeName,
        int accessTypeValue
    )
    {
        tx.put(tblKey(SecAccessTypes.ACCESS_TYPE_NAME, accessTypeName), accessTypeName);
        tx.put(tblKey(SecAccessTypes.ACCESS_TYPE_VALUE, accessTypeName), Integer.toString(accessTypeValue));
    }

    private static void secTypeRules(
        EtcdTransaction tx,
        String domainName,
        String typeName,
        int accessType
    )
    {
        final String pk = domainName + PRIMARY_KEY_DELI + typeName;
        tx.put(tblKey(SecTypeRules.DOMAIN_NAME, pk), domainName);
        tx.put(tblKey(SecTypeRules.TYPE_NAME, pk), typeName);
        tx.put(tblKey(SecTypeRules.ACCESS_TYPE, pk), Integer.toString(accessType));
    }

    private static void secDfltRoles(
        EtcdTransaction tx,
        String identityName,
        String roleName
    )
    {
        final String pk = identityName;
        tx.put(tblKey(SecDfltRoles.IDENTITY_NAME, pk), identityName);
        tx.put(tblKey(SecDfltRoles.ROLE_NAME, pk), roleName);
    }

    private static void secObjectProtection(
        EtcdTransaction tx,
        String objectPath,
        String creatorIdentityName,
        String ownerRoleName,
        String securityTypeName
    )
    {
        final String pk = objectPath;
        tx.put(tblKey(SecObjectProtection.OBJECT_PATH, pk), objectPath);
        tx.put(tblKey(SecObjectProtection.CREATOR_IDENTITY_NAME, pk), creatorIdentityName);
        tx.put(tblKey(SecObjectProtection.OWNER_ROLE_NAME, pk), ownerRoleName);
        tx.put(tblKey(SecObjectProtection.SECURITY_TYPE_NAME, pk), securityTypeName);
    }

    private static void secAclMap(
        EtcdTransaction tx,
        String objectPath,
        String roleName,
        int accessType
    )
    {
        final String pk = objectPath + PRIMARY_KEY_DELI + roleName;
        tx.put(tblKey(SecAclMap.OBJECT_PATH, pk), objectPath);
        tx.put(tblKey(SecAclMap.ROLE_NAME, pk), roleName);
        tx.put(tblKey(SecAclMap.ACCESS_TYPE, pk), Integer.toString(accessType)        );
    }

    private static void storPoolDefinitions(
        EtcdTransaction tx,
        String uuid,
        String poolName,
        String poolDspName
    )
    {
        final String pk = poolName;
        tx.put(tblKey(StorPoolDefinitions.UUID, pk), uuid);
        tx.put(tblKey(StorPoolDefinitions.POOL_NAME, pk), poolName);
        tx.put(tblKey(StorPoolDefinitions.POOL_DSP_NAME, pk), poolDspName);
    }

    private static void resourceGroups(
        EtcdTransaction tx,
        String uuid,
        String resourceGroupName,
        String resourceGroupDspName
    )
    {
        final String pk = resourceGroupName;
        tx.put(tblKey(ResourceGroups.UUID, pk), uuid);
        tx.put(tblKey(ResourceGroups.RESOURCE_GROUP_NAME, pk), resourceGroupName);
        tx.put(tblKey(ResourceGroups.RESOURCE_GROUP_DSP_NAME, pk), resourceGroupDspName);
    }

    private static void propsContainers(
        EtcdTransaction tx,
        String propsInstance,
        String propKey,
        String propValue
    )
    {
        tx.put(
            GeneratedDatabaseTables.DATABASE_SCHEMA_NAME + "/" +
                GeneratedDatabaseTables.PROPS_CONTAINERS.getName() + "/" +
                propsInstance + PRIMARY_KEY_DELI + propKey,
                propValue
        );
    }

    public static void migrate(EtcdTransaction tx)
    {
        // push init values
        secConfiguration(tx, "SECURITYLEVEL", "SecurityLevel", "NO_SECURITY");
        secConfiguration(tx, "AUTHREQUIRED", "AuthRequired", "false");

        secIdentities(tx, "SYSTEM", "SYSTEM", true, true);
        secIdentities(tx, "PUBLIC", "PUBLIC", true, true);

        secTypes(tx, "SYSTEM", "SYSTEM", true);
        secTypes(tx, "PUBLIC", "PUBLIC", true);
        secTypes(tx, "SHARED", "SHARED", true);
        secTypes(tx, "SYSADM", "SysAdm", true);
        secTypes(tx, "USER", "User", true);

        secRoles(tx, "SYSTEM", "SYSTEM", "SYSTEM", true, -1);
        secRoles(tx, "PUBLIC", "PUBLIC", "PUBLIC", true, 0);
        secRoles(tx, "SYSADM", "SysAdm", "SYSADM", true, -1);
        secRoles(tx, "USER", "User", "USER", true, 0);

        secIdRoleMap(tx, "SYSTEM", "SYSTEM");
        secIdRoleMap(tx, "PUBLIC", "PUBLIC");

        secAccessTypes(tx, "CONTROL", 15);
        secAccessTypes(tx, "CHANGE", 7);
        secAccessTypes(tx, "USE", 3);
        secAccessTypes(tx, "VIEW", 1);

        secTypeRules(tx, "SYSTEM", "SYSTEM", 15);
        secTypeRules(tx, "SYSTEM", "PUBLIC", 15);
        secTypeRules(tx, "SYSTEM", "SHARED", 15);
        secTypeRules(tx, "SYSTEM", "SYSADM", 15);
        secTypeRules(tx, "SYSTEM", "USER", 15);
        secTypeRules(tx, "PUBLIC", "SYSTEM", 3);
        secTypeRules(tx, "PUBLIC", "PUBLIC", 15);
        secTypeRules(tx, "PUBLIC", "SHARED", 7);
        secTypeRules(tx, "PUBLIC", "SYSADM", 3);
        secTypeRules(tx, "PUBLIC", "USER", 3);
        secTypeRules(tx, "SYSADM", "SYSTEM", 15);
        secTypeRules(tx, "SYSADM", "PUBLIC", 15);
        secTypeRules(tx, "SYSADM", "SHARED", 15);
        secTypeRules(tx, "SYSADM", "SYSADM", 15);
        secTypeRules(tx, "SYSADM", "USER", 15);
        secTypeRules(tx, "USER", "SYSTEM", 3);
        secTypeRules(tx, "USER", "PUBLIC", 7);
        secTypeRules(tx, "USER", "SHARED", 7);
        secTypeRules(tx, "USER", "SYSADM", 3);
        secTypeRules(tx, "USER", "USER", 15);

        secDfltRoles(tx, "SYSTEM", "SYSTEM");
        secDfltRoles(tx, "PUBLIC", "PUBLIC");

        secObjectProtection(tx, "/sys/controller/nodesMap", "SYSTEM", "SYSADM", "SHARED");
        secObjectProtection(tx, "/sys/controller/rscDfnMap", "SYSTEM", "SYSADM", "SHARED");
        secObjectProtection(tx, "/sys/controller/storPoolMap", "SYSTEM", "SYSADM", "SHARED");
        secObjectProtection(tx, "/sys/controller/conf", "SYSTEM", "SYSADM", "SYSTEM");
        secObjectProtection(tx, "/sys/controller/shutdown", "SYSTEM", "SYSADM", "SYSTEM");
        secObjectProtection(tx, "/storpooldefinitions/DFLTSTORPOOL", "SYSTEM", "SYSADM", "SHARED");
        secObjectProtection(tx, "/storpooldefinitions/DFLTDISKLESSSTORPOOL", "SYSTEM", "SYSADM", "SHARED");
        secObjectProtection(tx, "/resourcegroups/DFLTRSCGRP", "SYSTEM", "SYSADM", "SHARED");
        secObjectProtection(tx, "/sys/controller/rscGrpMap", "SYSTEM", "SYSTEM", "SYSTEM");
        secObjectProtection(tx, "/sys/controller/freeSpaceMgrMap", "SYSTEM", "SYSTEM", "SYSTEM");
        secObjectProtection(tx, "/sys/controller/keyValueStoreMap", "SYSTEM", "SYSTEM", "SYSTEM");

        secAclMap(tx, "/sys/controller/nodesMap", "SYSTEM", 15);
        secAclMap(tx, "/sys/controller/nodesMap", "USER", 7);
        secAclMap(tx, "/sys/controller/rscDfnMap", "SYSTEM", 15);
        secAclMap(tx, "/sys/controller/rscDfnMap", "USER", 7);
        secAclMap(tx, "/sys/controller/storPoolMap", "SYSTEM", 15);
        secAclMap(tx, "/sys/controller/storPoolMap", "USER", 7);
        secAclMap(tx, "/sys/controller/conf", "SYSTEM", 15);
        secAclMap(tx, "/storpooldefinitions/DFLTSTORPOOL", "PUBLIC", 7);
        secAclMap(tx, "/storpooldefinitions/DFLTSTORPOOL", "USER", 7);
        secAclMap(tx, "/storpooldefinitions/DFLTDISKLESSSTORPOOL", "PUBLIC", 7);
        secAclMap(tx, "/storpooldefinitions/DFLTDISKLESSSTORPOOL", "USER", 7);
        secAclMap(tx, "/sys/controller/nodesMap", "PUBLIC", 7);
        secAclMap(tx, "/sys/controller/rscDfnMap", "PUBLIC", 7);
        secAclMap(tx, "/sys/controller/storPoolMap", "PUBLIC", 7);
        secAclMap(tx, "/sys/controller/conf", "PUBLIC", 1);
        secAclMap(tx, "/resourcegroups/DFLTRSCGRP", "PUBLIC", 7);
        secAclMap(tx, "/resourcegroups/DFLTRSCGRP", "USER", 7);
        secAclMap(tx, "/sys/controller/rscGrpMap", "SYSTEM", 15);
        secAclMap(tx, "/sys/controller/freeSpaceMgrMap", "SYSTEM", 15);
        secAclMap(tx, "/sys/controller/keyValueStoreMap", "SYSTEM", 15);
        secAclMap(tx, "/sys/controller/shutdown", "SYSTEM", 15);

        storPoolDefinitions(tx, "f51611c6-528f-4793-a87a-866d09e6733a", "DFLTSTORPOOL", "DfltStorPool");
        storPoolDefinitions(tx, "622807eb-c8c4-44f0-b03d-a08173c8fa1b", "DFLTDISKLESSSTORPOOL", "DfltDisklessStorPool");

        resourceGroups(tx, "a52e934a-9fd9-44cb-9db1-716dcd13aae3", "DFLTRSCGRP", "DfltRscGrp");

        propsContainers(tx, "/CTRLCFG", "defaultDebugSslConnector", "DebugSslConnector");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/bindaddress", "::0");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/enabled", "true");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/keyPasswd", "linstor");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/keyStore", "ssl/keystore.jks");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/keyStorePasswd", "linstor");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/port", "3373");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/sslProtocol", "TLSv1.2");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/trustStore", "ssl/certificates.jks");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/trustStorePasswd", "linstor");
        propsContainers(tx, "/CTRLCFG", "netcom/DebugSslConnector/type", "ssl");
        propsContainers(tx, "/CTRLCFG", "netcom/PlainConnector/bindaddress", "::0");
        propsContainers(tx, "/CTRLCFG", "netcom/PlainConnector/enabled", "true");
        propsContainers(tx, "/CTRLCFG", "netcom/PlainConnector/port", "3376");
        propsContainers(tx, "/CTRLCFG", "netcom/PlainConnector/type", "plain");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/bindaddress", "::0");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/enabled", "true");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/keyPasswd", "linstor");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/keyStore", "ssl/keystore.jks");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/keyStorePasswd", "linstor");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/port", "3377");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/sslProtocol", "TLSv1.2");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/trustStore", "ssl/certificates.jks");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/trustStorePasswd", "linstor");
        propsContainers(tx, "/CTRLCFG", "netcom/SslConnector/type", "ssl");
        propsContainers(tx, "/CTRLCFG", "defaultPlainConSvc", "PlainConnector");
        propsContainers(tx, "/CTRLCFG", "defaultSslConSvc", "SslConnector");

        tx.put(EtcdUtils.LINSTOR_PREFIX + "DBHISTORY/version", "1");
    }
}
