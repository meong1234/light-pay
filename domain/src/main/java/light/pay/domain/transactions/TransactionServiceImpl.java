package light.pay.domain.transactions;

import light.pay.api.errors.Errors;
import light.pay.api.response.Response;
import light.pay.api.transactions.TransactionService;
import light.pay.api.transactions.TransactionStatus;
import light.pay.api.transactions.request.InitiateTransactionRequest;
import light.pay.api.transactions.response.TransactionDTO;
import light.pay.domain.transactions.entity.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.function.Function;

import static net.logstash.logback.marker.Markers.append;

public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Response<TransactionDTO> initiateTransaction(InitiateTransactionRequest request) {
        logger.info(append("request", request.toString()), "receiving initiateTransaction");
        Response<Transaction> byReferenceID = transactionRepository.getByReferenceID(request.getReferenceID());
        if (byReferenceID.isSuccess()) {
            logger.warn(append("reference_id", request.getReferenceID()), "reference_id already exists");
            return Response.createErrorResponse(Errors.TRANSACTION_ALREADY_EXISTS_ERROR_CODE, "reference_id", "");
        }

        Transaction transaction = Transaction.builder()
                .transactionID(request.getTransactionID())
                .referenceID(request.getReferenceID())
                .creditedWallet(request.getCreditedWallet())
                .debitedWallet(request.getDebitedWallet())
                .description(request.getDescription())
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .status(TransactionStatus.INITIATED)
                .createdAt(Timestamp.from(Instant.now()))
                .updatedAt(Timestamp.from(Instant.now()))
                .build();
        return transactionRepository.insert(transaction)
                .map(mapTransactionToDTO(request));
    }


    @Override
    public Response<TransactionDTO> completeTransaction(String transactionId) {
        logger.info(append("request", transactionId), "receiving completeTransaction");
        Response<Transaction> byTransactionID = transactionRepository.getByTransactionID(transactionId);
        if (!byTransactionID.isSuccess()) {
            logger.warn(append("transaction_id", transactionId), "transaction_id not found");
            return (Response) byTransactionID;
        }

        Transaction transaction = byTransactionID.getData();
        Transaction completedTransaction = transaction.markAsComplete();

        return transactionRepository.updateStatus(completedTransaction)
                .map(v -> mapTransactionToDTO(completedTransaction));
    }

    @Override
    public Response<TransactionDTO> findTransaction(String transactionId) {
        logger.info(append("request", transactionId), "receiving findTransaction");
        return transactionRepository.getByTransactionID(transactionId)
                .map(this::mapTransactionToDTO);
    }

    private Function<Long, TransactionDTO> mapTransactionToDTO(InitiateTransactionRequest request) {
        return trx -> TransactionDTO.builder()
                .transactionID(request.getTransactionID())
                .amount(request.getAmount())
                .creditedWallet(request.getCreditedWallet())
                .debitedWallet(request.getDebitedWallet())
                .description(request.getDescription())
                .referenceID(request.getReferenceID())
                .transactionType(request.getTransactionType())
                .createdAt(OffsetDateTime.now())
                .status(TransactionStatus.INITIATED)
                .build();
    }

    private TransactionDTO mapTransactionToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .transactionID(transaction.getTransactionID())
                .amount(transaction.getAmount())
                .creditedWallet(transaction.getCreditedWallet())
                .debitedWallet(transaction.getDebitedWallet())
                .description(transaction.getDescription())
                .referenceID(transaction.getReferenceID())
                .transactionType(transaction.getTransactionType())
                .createdAt(OffsetDateTime.ofInstant(transaction.getCreatedAt().toInstant(), ZoneId.of("UTC")))
                .status(transaction.getStatus())
                .build();
    }
}
