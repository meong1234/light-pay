package light.pay.api.wallets;

import light.pay.api.errors.Response;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.api.wallets.request.TopupWalletRequest;
import light.pay.api.wallets.request.TransferRequest;
import light.pay.api.wallets.response.WalletDTO;

public interface WalletService {
    Response<String> createWallet(CreateWalletRequest request);
    Response<WalletDTO> findWallet(String walletId);
    Response<WalletDTO> findWalletByUserId(String userId);
    Response<Void> topupWallet(TopupWalletRequest request);
    Response<Void> transfer(TransferRequest request);
}
