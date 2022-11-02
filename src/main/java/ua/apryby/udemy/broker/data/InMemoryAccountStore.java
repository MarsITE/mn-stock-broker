package ua.apryby.udemy.broker.data;

import jakarta.inject.Singleton;
import ua.apryby.udemy.broker.wallet.DepositFiatMoney;
import ua.apryby.udemy.broker.wallet.Wallet;
import ua.apryby.udemy.broker.watchlist.WatchList;

import java.math.BigDecimal;
import java.util.*;

@Singleton
public class InMemoryAccountStore {

    public static final UUID ACCOUNT_ID = UUID.fromString("8249d700-5aae-11ed-9ced-ffe07b3c2396");

    private final HashMap<UUID, WatchList> watchListsPerAccount = new HashMap<>();
    private final HashMap<UUID, Map<UUID, Wallet>> walletsPerAccount = new HashMap<>();

    public WatchList getWatchList(final UUID accountId) {
        return watchListsPerAccount.getOrDefault(accountId, new WatchList());
    }

    public WatchList updateWatchList(final UUID accountId, final WatchList watchList) {
        watchListsPerAccount.put(accountId, watchList);
        return getWatchList(accountId);
    }

    public void deleteWatchList(final UUID accountId) {
        watchListsPerAccount.remove(accountId);
    }

    public Collection<Wallet> getWallets(UUID accountId) {
        return Optional.ofNullable(walletsPerAccount.get(accountId))
                .orElse(new HashMap<>())
                .values();
    }

    public Wallet depositToWallet(DepositFiatMoney deposit) {

        final var wallets = Optional.ofNullable(
                walletsPerAccount.get(deposit.accountId())
        ).orElse(new HashMap<>());

        var oldWallet = Optional.ofNullable(wallets.get(deposit.walletId())
        ).orElse(
                new Wallet(ACCOUNT_ID, deposit.walletId(), deposit.symbol(), BigDecimal.ZERO, BigDecimal.ZERO)
        );

        var newWallet = oldWallet.addAvailable(deposit.amount());

        wallets.put(newWallet.walletId(), newWallet);
        walletsPerAccount.put(newWallet.accountId(), wallets);

        return newWallet;
    }
}
