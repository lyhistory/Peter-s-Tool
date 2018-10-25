package io.stormbird.wallet.service;

import io.stormbird.wallet.BuildConfig;
import io.stormbird.wallet.entity.NetworkInfo;
import io.stormbird.wallet.entity.Wallet;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmManager {

    private final Map<String, RealmConfiguration> realmConfigurations = new HashMap<>();

    public Realm getRealmInstance(NetworkInfo networkInfo, Wallet wallet) {
        String name = getName(networkInfo, wallet);
        RealmConfiguration config = realmConfigurations.get(name);
        if (config == null) {
            config = new RealmConfiguration.Builder()
                    .name(name)
                    .schemaVersion(BuildConfig.DB_VERSION)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realmConfigurations.put(name, config);
        }
        return Realm.getInstance(config);
    }

    private String getName(NetworkInfo networkInfo, Wallet wallet) {
        return wallet.address + "-" + networkInfo.name + "-db.realm";
    }

    public Realm getERC721RealmInstance(Wallet wallet) {
        String name = get721Name(wallet);
        RealmConfiguration config = realmConfigurations.get(name);
        if (config == null) {
            config = new RealmConfiguration.Builder()
                    .name(name)
                    .schemaVersion(BuildConfig.DB_VERSION)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            realmConfigurations.put(name, config);
        }
        return Realm.getInstance(config);
    }

    private String get721Name(Wallet wallet) {
        return wallet.address + "-721-db.realm";
    }
}
