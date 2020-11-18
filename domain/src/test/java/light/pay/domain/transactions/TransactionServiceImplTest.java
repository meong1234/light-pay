package light.pay.domain.transactions;

import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.response.Response;
import light.pay.api.transactions.TransactionService;
import light.pay.api.transactions.TransactionStatus;
import light.pay.api.transactions.request.InitiateTransactionRequest;
import light.pay.api.transactions.response.TransactionDTO;
import light.pay.domain.transactions.entity.Transaction;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    private TransactionService transactionService;
    private TransactionRepository mockRepository;
    private EasyRandom objectGenerator = new EasyRandom();

    @BeforeEach
    void setUp() {
        mockRepository = mock(TransactionRepository.class);
        transactionService = new TransactionServiceImpl(mockRepository);
    }

    @Test
    void shouldCallRepositoryInsertOnInitiateTransaction() {
        InitiateTransactionRequest request = objectGenerator.nextObject(InitiateTransactionRequest.class);

        when(mockRepository.insert(argThat(isValidTransaction(request)))).thenReturn(Response.createSuccessResponse(1L));
        when(mockRepository.getByReferenceID(eq(request.getReferenceID()))).thenReturn(Response.createErrorResponse(Errors.TRANSACTION_NOT_EXISTS_ERROR_CODE, "reference_id", ""));

        Response<TransactionDTO> response = transactionService.initiateTransaction(request);
        assertTrue(response.isSuccess());

        verify(mockRepository, times(1)).insert(any(Transaction.class));
        verify(mockRepository, times(1)).getByReferenceID(anyString());
    }

    @Test
    void shouldErrorWhenInitiateDuplicateTransaction() {
        InitiateTransactionRequest request = objectGenerator.nextObject(InitiateTransactionRequest.class);
        Transaction transaction = objectGenerator.nextObject(Transaction.class);

        when(mockRepository.insert(argThat(isValidTransaction(request)))).thenReturn(Response.createSuccessResponse(1L));
        when(mockRepository.getByReferenceID(eq(request.getReferenceID()))).thenReturn(Response.createSuccessResponse(transaction));

        Response<TransactionDTO> response = transactionService.initiateTransaction(request);
        assertFalse(response.isSuccess());
        List<Error> errors = response.getErrors();
        assertEquals(Errors.TRANSACTION_ALREADY_EXISTS_ERROR_CODE, errors.get(0).getCode());

        verify(mockRepository, times(0)).insert(any(Transaction.class));
        verify(mockRepository, times(1)).getByReferenceID(anyString());
    }

    @Test
    void shouldCallRepositoryFindByUserIdOnFindWallet() {
        String request = objectGenerator.nextObject(String.class);
        Transaction data = objectGenerator.nextObject(Transaction.class);

        when(mockRepository.getByTransactionID(eq(request))).thenReturn(Response.createSuccessResponse(data));

        Response<TransactionDTO> response = transactionService.findTransaction(request);
        assertTrue(response.isSuccess());
        TransactionDTO actual = response.getData();
        assertThat(actual).isEqualToIgnoringGivenFields(data, "createdAt");

        verify(mockRepository, times(1)).getByTransactionID(anyString());
    }

    @Test
    void shouldCallRepositoryUpdateStatusOnMarkAsComplete() {
        String request = objectGenerator.nextObject(String.class);
        Transaction transaction = objectGenerator.nextObject(Transaction.class);
        Transaction completedTransaction = transaction.markAsComplete();

        when(mockRepository.updateStatus(any(Transaction.class))).thenReturn(Response.createSuccessResponse(null));
        when(mockRepository.getByTransactionID(eq(request))).thenReturn(Response.createSuccessResponse(transaction));

        Response<TransactionDTO> response = transactionService.completeTransaction(request);
        assertTrue(response.isSuccess());

        TransactionDTO actual = response.getData();
        assertThat(actual).isEqualToIgnoringGivenFields(completedTransaction, "createdAt");

        verify(mockRepository, times(1)).updateStatus(any(Transaction.class));
        verify(mockRepository, times(1)).getByTransactionID(anyString());
    }

    @Test
    void shouldTransactionNotFoundErrorWhenMarkComplete() {
        String request = objectGenerator.nextObject(String.class);

        Response expectedResponse = Response.createErrorResponse(Errors.TRANSACTION_NOT_EXISTS_ERROR_CODE, "transaction_id", "");
        when(mockRepository.getByTransactionID(eq(request))).thenReturn(expectedResponse);

        Response<TransactionDTO> response = transactionService.completeTransaction(request);
        assertEquals(expectedResponse, response);

        verify(mockRepository, times(0)).updateStatus(any(Transaction.class));
        verify(mockRepository, times(1)).getByTransactionID(anyString());
    }

    private ArgumentMatcher<Transaction> isValidTransaction(InitiateTransactionRequest request) {
        return trx -> trx.getTransactionID().equals(request.getTransactionID())
                && trx.getAmount().equals(request.getAmount())
                && trx.getReferenceID().equals(request.getReferenceID())
                && trx.getDescription().equals(request.getDescription())
                && trx.getCreditedWallet().equals(request.getCreditedWallet())
                && trx.getDebitedWallet().equals(request.getDebitedWallet())
                && trx.getStatus().equals(TransactionStatus.INITIATED)
                && trx.getTransactionType().equals(request.getTransactionType());
    }
}