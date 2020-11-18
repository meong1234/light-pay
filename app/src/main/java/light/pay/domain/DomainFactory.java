package light.pay.domain;

import light.pay.api.accounts.AccountService;
import light.pay.api.gateway.GatewayService;
import light.pay.api.transactions.TransactionService;
import light.pay.api.wallets.WalletService;
import light.pay.domain.accounts.AccountRepository;
import light.pay.domain.accounts.AccountServiceImpl;
import light.pay.domain.accounts.repository.AccountRepositoryImpl;
import light.pay.domain.gateway.GatewayServiceImpl;
import light.pay.domain.transactions.TransactionRepository;
import light.pay.domain.transactions.TransactionServiceImpl;
import light.pay.domain.transactions.repository.TransactionRepositoryImpl;
import light.pay.domain.wallets.WalletRepository;
import light.pay.domain.wallets.WalletServiceImpl;
import light.pay.domain.wallets.repository.WalletRepositoryImpl;

public class DomainFactory {

    public static GatewayService createService() {
        WalletRepository walletRepository = new WalletRepositoryImpl();

        AccountRepository accountRepository = new AccountRepositoryImpl();

        TransactionRepository transactionRepository = new TransactionRepositoryImpl();

        WalletService walletService = new WalletServiceImpl(walletRepository);

        AccountService accountService = new AccountServiceImpl(accountRepository);

        TransactionService transactionService = new TransactionServiceImpl(transactionRepository);

        return new GatewayServiceImpl(accountService, walletService, transactionService);
    }
}
