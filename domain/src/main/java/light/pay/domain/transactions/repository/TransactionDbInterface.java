package light.pay.domain.transactions.repository;

import light.pay.api.transactions.TransactionStatus;
import light.pay.domain.accounts.repository.AccountMapper;
import light.pay.domain.transactions.entity.Transaction;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.Timestamp;

@RegisterMapper(TransactionMapper.class)
public interface TransactionDbInterface {
    @SqlUpdate("INSERT INTO transactions (transaction_id, reference_id, credited_wallet, debited_wallet, description," +
            " amount, transaction_type, status, created_at, updated_at) " +
            "VALUES (:w.transactionID, :w.referenceID, :w.creditedWallet, :w.debitedWallet, :w.description, :w.amount, :w.transactionType, :w.status, :w.createdAt, :w.updatedAt)")
    @GetGeneratedKeys
    Long insert(@BindBean("w") Transaction transaction);

    @SqlQuery("SELECT id, transaction_id, reference_id, credited_wallet, debited_wallet, description, amount," +
            " transaction_type, status, created_at, updated_at FROM transactions WHERE transaction_id = :transaction_id")
    Transaction getByTransactionId(@Bind("transaction_id") String transactionId);

    @SqlQuery("SELECT id, transaction_id, reference_id, credited_wallet, debited_wallet, description, amount," +
            " transaction_type, status, created_at, updated_at FROM transactions WHERE reference_id = :reference_id")
    Transaction getByReferenceId(@Bind("reference_id") String referenceId);

    @SqlUpdate("UPDATE transactions SET status = :status, updated_at = :updated_at  where transaction_id = :transaction_id")
    int updateBalance(@Bind("transaction_id") String transactionId, @Bind("status") TransactionStatus newStatus, @Bind("updated_at") Timestamp updatedAt);
}
