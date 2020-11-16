package light.pay.domain.transactions.repository;

import light.pay.api.transactions.TransactionStatus;
import light.pay.api.transactions.TransactionType;
import light.pay.domain.transactions.entity.Transaction;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionMapper implements ResultSetMapper<Transaction> {
    @Override
    public Transaction map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Transaction transaction = Transaction.builder()
                .id(r.getLong("id"))
                .transactionID(r.getString("transaction_id"))
                .referenceID(r.getString("reference_id"))
                .creditedWallet(r.getString("credited_wallet"))
                .debitedWallet(r.getString("debited_wallet"))
                .description(r.getString("description"))
                .amount(r.getLong("amount"))
                .transactionType(TransactionType.valueOf(r.getString("transaction_type")))
                .status(TransactionStatus.valueOf(r.getString("status")))
                .createdAt(r.getTimestamp("created_at"))
                .updatedAt(r.getTimestamp("updated_at"))
                .build();

        return transaction;
    }
}
