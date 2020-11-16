package light.pay.domain.transactions.repository;

import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.commons.db.Repository;
import light.pay.domain.transactions.TransactionRepository;
import light.pay.domain.transactions.entity.Transaction;

import java.util.Collections;

public class TransactionRepositoryImpl extends Repository<TransactionDbInterface> implements TransactionRepository {
    @Override
    public Response<Long> insert(Transaction transaction) {
        return execute(TransactionDbInterface.class, repo -> repo.insert(transaction));
    }

    @Override
    public Response<Transaction> getByTransactionID(String transactionID) {
        return execute(TransactionDbInterface.class, repo -> repo.getByTransactionId(transactionID))
                .validate(this::isExists, Collections.singletonList(new Error( "transaction_id",  Errors.TRANSACTION_NOT_EXISTS_ERROR_CODE,"")));
    }

    @Override
    public Response<Transaction> getByReferenceID(String referenceId) {
        return execute(TransactionDbInterface.class, repo -> repo.getByReferenceId(referenceId))
                .validate(this::isExists, Collections.singletonList(new Error( "reference_id",  Errors.TRANSACTION_NOT_EXISTS_ERROR_CODE,"")));
    }

    @Override
    public Response<Void> updateStatus(Transaction transaction) {
        return execute(TransactionDbInterface.class, repo -> repo.updateBalance(transaction.getTransactionID(), transaction.getStatus(), transaction.getUpdatedAt()))
                .map(i -> null);
    }
}
