package light.pay.domain.wallets;

import light.pay.api.errors.Errors;
import light.pay.api.errors.Response;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.api.wallets.request.TopupWalletRequest;
import light.pay.api.wallets.request.TransferRequest;
import light.pay.api.wallets.response.WalletDTO;
import light.pay.domain.wallets.entity.Wallet;

public class WalletServiceImpl implements WalletService {

    private WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Response<String> createWallet(CreateWalletRequest request) {
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
        return walletRepository.getByWalletId(walletId)
                .map(this::mapWalletToDTO);
    }

    @Override
    public Response<WalletDTO> findWalletByUserId(String userId) {
        return walletRepository.getByUserId(userId)
                .map(this::mapWalletToDTO);
    }

    @Override
    public Response<Void> topupWallet(TopupWalletRequest request) {
        Response<Wallet> byWalletId = walletRepository.getByWalletId(request.getWalletID());
        if (!byWalletId.isSuccess()) {
            return (Response) byWalletId;
        }
        Wallet wallet = byWalletId.getData();

        return walletRepository.updateBalance(wallet.addBalance(request.getAmount()));
    }

    @Override
    public Response<Void> transfer(TransferRequest request) {
        Response<Wallet> sourceWalletById = walletRepository.getByWalletId(request.getSourceID());
        if (!sourceWalletById.isSuccess()) {
            return (Response) sourceWalletById;
        }

        Wallet sourceWalletByIdData = sourceWalletById.getData();
        if (sourceWalletByIdData.getBalance() < request.getAmount()) {
            return Response.createErrorResponse(Errors.USER_BALANCE_IS_NOT_ENOUGH_ERROR_CODE, "amount", "");
        }

        Response<Wallet> targetWalletById = walletRepository.getByWalletId(request.getTargetID());
        if (!targetWalletById.isSuccess()) {
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
