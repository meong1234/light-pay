package light.pay.domain.wallets;

import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.api.wallets.request.TopupWalletRequest;
import light.pay.api.wallets.request.TransferRequest;
import light.pay.api.wallets.response.WalletDTO;
import light.pay.domain.wallets.entity.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.logstash.logback.marker.Markers.append;

public class WalletServiceImpl implements WalletService {
    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Response<String> createWallet(CreateWalletRequest request) {
        logger.info(append("request", request.toString()), "receiving createWallet");

        Wallet wallet = Wallet.builder()
                .userID(request.getUserID())
                .walletID(request.getWalletID())
                .balance(0L) //new created wallet always start with 0 balance
                .build();
        return walletRepository.insert(wallet)
                .map(id -> request.getWalletID());
    }

    @Override
    public Response<WalletDTO> findWallet(String walletId) {
        logger.info(append("request", walletId), "receiving findWallet");
        return walletRepository.getByWalletId(walletId)
                .map(this::mapWalletToDTO);
    }

    @Override
    public Response<WalletDTO> findWalletByUserId(String userId) {
        logger.info(append("request", userId), "receiving findWalletByUserId");
        return walletRepository.getByUserId(userId)
                .map(this::mapWalletToDTO);
    }

    @Override
    public Response<Void> topupWallet(TopupWalletRequest request) {
        logger.info(append("request", request.toString()), "receiving topupWallet");
        Response<Wallet> byWalletId = walletRepository.getByWalletId(request.getWalletID());
        if (!byWalletId.isSuccess()) {
            logger.warn(append("wallet_id", request.getWalletID()), "wallet not found");
            return (Response) byWalletId;
        }
        Wallet wallet = byWalletId.getData();

        return walletRepository.updateBalance(wallet.addBalance(request.getAmount()));
    }

    @Override
    public Response<Void> transfer(TransferRequest request) {
        logger.info(append("request", request.toString()), "receiving transfer");
        Response<Wallet> sourceWalletById = walletRepository.getByWalletId(request.getSourceID());
        if (!sourceWalletById.isSuccess()) {
            logger.warn(append("wallet_id", request.getSourceID()), "wallet not found");
            return (Response) sourceWalletById;
        }

        Wallet sourceWalletByIdData = sourceWalletById.getData();
        if (sourceWalletByIdData.getBalance() < request.getAmount()) {
            return Response.createErrorResponse(Errors.USER_BALANCE_IS_NOT_ENOUGH_ERROR_CODE, "amount", "");
        }

        Response<Wallet> targetWalletById = walletRepository.getByWalletId(request.getTargetID());
        if (!targetWalletById.isSuccess()) {
            logger.warn(append("wallet_id", request.getTargetID()), "wallet not found");
            return (Response) targetWalletById;
        }

        Wallet sourceWallet = sourceWalletByIdData
                .subtractBalance(request.getAmount());

        Wallet targetWallet = targetWalletById
                .getData()
                .addBalance(request.getAmount());

        return walletRepository.updateBalance(sourceWallet)
                .flatMap(v -> walletRepository.updateBalance(targetWallet));
    }

    private WalletDTO mapWalletToDTO(Wallet wallet) {
        return WalletDTO.builder()
                .walletId(wallet.getWalletID())
                .balance(wallet.getBalance())
                .build();
    }
}
