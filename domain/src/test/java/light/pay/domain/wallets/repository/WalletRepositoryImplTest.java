package light.pay.domain.wallets.repository;

import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.response.Response;
import light.pay.commons.db.DatabaseUtils;
import light.pay.domain.wallets.WalletRepository;
import light.pay.domain.wallets.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletRepositoryImplTest {

    private WalletRepository walletRepository = new WalletRepositoryImpl();

    @BeforeEach
    void setUp() {
        DatabaseUtils.truncate("wallets");
    }

    @Test
    void insertShouldExistsInDB() {
        String userID = UUID.randomUUID().toString();
        String walletId = UUID.randomUUID().toString();
        Wallet wallet = Wallet.builder()
                .walletID(walletId)
                .userID(userID)
                .balance(0L)
                .build();

        walletRepository.insert(wallet);

        Response<Wallet> response = walletRepository.getByUserId(userID);
        assertTrue(response.isSuccess());

        Wallet actual = response.getData();

        assertEquals(wallet.getUserID(), actual.getUserID());
        assertEquals(wallet.getWalletID(), actual.getWalletID());
        assertEquals(wallet.getBalance(), actual.getBalance());
    }

    @Test
    void insertShouldExistsInDBUsingGetByWalletId() {
        String userID = UUID.randomUUID().toString();
        String walletId = UUID.randomUUID().toString();
        Wallet wallet = Wallet.builder()
                .walletID(walletId)
                .userID(userID)
                .balance(0L)
                .build();

        walletRepository.insert(wallet);

        Response<Wallet> response = walletRepository.getByWalletId(walletId);
        assertTrue(response.isSuccess());

        Wallet actual = response.getData();

        assertEquals(wallet.getUserID(), actual.getUserID());
        assertEquals(wallet.getWalletID(), actual.getWalletID());
        assertEquals(wallet.getBalance(), actual.getBalance());
    }

    @Test
    void shouldUpdateBalanceSuccessfullyInDb() {
        String userID = UUID.randomUUID().toString();
        String walletId = UUID.randomUUID().toString();
        Wallet wallet = Wallet.builder()
                .walletID(walletId)
                .userID(userID)
                .balance(0L)
                .build();

        walletRepository.insert(wallet);

        walletRepository.updateBalance(wallet.addBalance(100L));

        Response<Wallet> response = walletRepository.getByUserId(userID);
        assertTrue(response.isSuccess());

        Wallet actual = response.getData();

        assertEquals(wallet.getUserID(), actual.getUserID());
        assertEquals(wallet.getWalletID(), actual.getWalletID());
        assertEquals(100L, actual.getBalance());
    }

    @Test
    void shouldReturnWalletNotFoundErrorIfNotExistsInDBAndByUserId() {
        Response<Wallet> response = walletRepository.getByUserId("sebuahid");
        assertFalse(response.isSuccess());
        List<Error> errors = response.getErrors();
        assertEquals(Errors.WALLET_NOT_FOUND_ERROR_CODE, errors.get(0).getCode());
        assertEquals("user_id", errors.get(0).getEntity());
    }

    @Test
    void shouldReturnWalletNotFoundErrorIfNotExistsInDB() {
        Response<Wallet> response = walletRepository.getByWalletId("sebuahid");
        assertFalse(response.isSuccess());
        List<Error> errors = response.getErrors();
        assertEquals(Errors.WALLET_NOT_FOUND_ERROR_CODE, errors.get(0).getCode());
        assertEquals("wallet_id", errors.get(0).getEntity());
    }
}