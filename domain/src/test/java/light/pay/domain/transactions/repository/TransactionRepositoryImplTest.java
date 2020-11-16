package light.pay.domain.transactions.repository;

import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.commons.db.DatabaseUtils;
import light.pay.domain.transactions.TransactionRepository;
import light.pay.domain.transactions.entity.Transaction;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryImplTest {

    private TransactionRepository transactionRepository = new TransactionRepositoryImpl();
    private EasyRandom objectGenerator = new EasyRandom();

    @BeforeEach
    void setUp() {
        DatabaseUtils.truncate("transactions");
    }

    @Test
    void insertShouldExistsInDB() {
        Transaction transaction = objectGenerator
                .nextObject(Transaction.class)
                .withId(null);

        transactionRepository.insert(transaction);

        Response<Transaction> response = transactionRepository.getByTransactionID(transaction.getTransactionID());
        assertTrue(response.isSuccess());

        Transaction actual = response.getData();

        assertThat(actual).isEqualToIgnoringGivenFields(transaction, "id");
    }

    @Test
    void shouldReturnWalletNotFoundErrorIfNotExistsInDBAndByUserId() {
        Response<Transaction> response = transactionRepository.getByTransactionID("sebuahid");
        assertFalse(response.isSuccess());
        List<Error> errors = response.getErrors();
        assertEquals(Errors.TRANSACTION_NOT_EXISTS_ERROR_CODE, errors.get(0).getCode());
        assertEquals("transaction_id", errors.get(0).getEntity());
    }

    @Test
    void shouldReturnWalletNotFoundErrorIfNotExistsInDB() {
        Response<Transaction> response = transactionRepository.getByReferenceID("sebuahid");
        assertFalse(response.isSuccess());
        List<Error> errors = response.getErrors();
        assertEquals(Errors.TRANSACTION_NOT_EXISTS_ERROR_CODE, errors.get(0).getCode());
        assertEquals("reference_id", errors.get(0).getEntity());
    }

    @Test
    void shouldUpdateBalanceSuccessfullyInDb() {
        Transaction transaction = objectGenerator.nextObject(Transaction.class);

        Transaction expected = transaction.markAsComplete();

        transactionRepository.insert(transaction);
        transactionRepository.updateStatus(expected);
        Response<Transaction> response = transactionRepository.getByTransactionID(transaction.getTransactionID());

        assertTrue(response.isSuccess());

        Transaction actual = response.getData();

        assertThat(actual).isEqualToIgnoringGivenFields(expected, "id");
    }
}