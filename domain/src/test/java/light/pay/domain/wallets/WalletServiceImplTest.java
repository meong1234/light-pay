package light.pay.domain.wallets;

import light.pay.api.errors.Error;
import light.pay.api.errors.Errors;
import light.pay.api.response.Response;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.api.wallets.request.TopupWalletRequest;
import light.pay.api.wallets.request.TransferRequest;
import light.pay.api.wallets.response.WalletDTO;
import light.pay.domain.wallets.entity.Wallet;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WalletServiceImplTest {

    private WalletService walletService;
    private WalletRepository mockRepository;
    private EasyRandom objectGenerator = new EasyRandom();

    @BeforeEach
    void setUp() {
        mockRepository = mock(WalletRepository.class);
        walletService = new WalletServiceImpl(mockRepository);
    }

    @Test
    void shouldCallRepositoryInsertOnCrateNewAccount() {
        CreateWalletRequest request = objectGenerator.nextObject(CreateWalletRequest.class);

        when(mockRepository.insert(argThat(isValidWallet(request)))).thenReturn(Response.createSuccessResponse(1L));

        Response<String> response = walletService.createWallet(request);
        assertTrue(response.isSuccess());
        assertEquals(request.getWalletID(), response.getData());

        verify(mockRepository, times(1)).insert(any(Wallet.class));
    }

    @Test
    void shouldCallRepositoryFindByUserIdOnFindWallet() {
        String request = objectGenerator.nextObject(String.class);
        Wallet data = objectGenerator.nextObject(Wallet.class);

        when(mockRepository.getByUserId(eq(request))).thenReturn(Response.createSuccessResponse(data));

        Response<WalletDTO> response = walletService.findWalletByUserId(request);
        assertTrue(response.isSuccess());
        WalletDTO actual = response.getData();
        assertEquals(data.getWalletID(), actual.getWalletId());
        assertEquals(data.getBalance(), actual.getBalance());

        verify(mockRepository, times(1)).getByUserId(anyString());
    }

    @Test
    void shouldCallRepositoryFindByWalletIdOnFindWallet() {
        String request = objectGenerator.nextObject(String.class);
        Wallet data = objectGenerator.nextObject(Wallet.class);

        when(mockRepository.getByWalletId(eq(request))).thenReturn(Response.createSuccessResponse(data));

        Response<WalletDTO> response = walletService.findWallet(request);
        assertTrue(response.isSuccess());
        WalletDTO actual = response.getData();
        assertEquals(data.getWalletID(), actual.getWalletId());
        assertEquals(data.getBalance(), actual.getBalance());

        verify(mockRepository, times(1)).getByWalletId(anyString());
    }

    @Test
    void shouldCallRepositoryUpdateBalanceOnTopup() {
        TopupWalletRequest request = objectGenerator.nextObject(TopupWalletRequest.class);
        Wallet data = objectGenerator.nextObject(Wallet.class);
        Wallet addBalance = data.addBalance(request.getAmount());

        when(mockRepository.getByWalletId(eq(request.getWalletID()))).thenReturn(Response.createSuccessResponse(data));
        when(mockRepository.updateBalance(eq(addBalance))).thenReturn(Response.createSuccessResponse(null));

        Response<Void> response = walletService.topupWallet(request);
        assertTrue(response.isSuccess());

        verify(mockRepository, times(1)).getByWalletId(anyString());
        verify(mockRepository, times(1)).updateBalance(eq(addBalance));
    }

    @Test
    void shouldCallRepositoryUpdateBalanceOnTransfer() {
        TransferRequest request = objectGenerator
                .nextObject(TransferRequest.class)
                .withAmount(100L);

        Wallet sourceWallet = objectGenerator
                .nextObject(Wallet.class)
                .withBalance(200L);

        Wallet targetWallet = objectGenerator
                .nextObject(Wallet.class)
                .withBalance(300L);

        Wallet subtractBalance = sourceWallet.subtractBalance(request.getAmount());
        Wallet addBalance = targetWallet.addBalance(request.getAmount());

        when(mockRepository.getByWalletId(eq(request.getSourceID())))
                .thenReturn(Response.createSuccessResponse(sourceWallet));

        when(mockRepository.getByWalletId(eq(request.getTargetID())))
                .thenReturn(Response.createSuccessResponse(targetWallet));

        when(mockRepository.updateBalance(eq(addBalance))).thenReturn(Response.createSuccessResponse(null));
        when(mockRepository.updateBalance(eq(subtractBalance))).thenReturn(Response.createSuccessResponse(null));

        Response<Void> response = walletService.transfer(request);
        assertTrue(response.isSuccess());

        verify(mockRepository, times(2)).getByWalletId(anyString());
        verify(mockRepository, times(1)).updateBalance(eq(addBalance));
        verify(mockRepository, times(1)).updateBalance(eq(subtractBalance));
    }

    @Test
    void shouldReturnNotEnoughBalanceErrorOnTransfer() {
        TransferRequest request = objectGenerator
                .nextObject(TransferRequest.class)
                .withAmount(100L);

        Wallet sourceWallet = objectGenerator
                .nextObject(Wallet.class)
                .withBalance(50L);

        Wallet targetWallet = objectGenerator
                .nextObject(Wallet.class)
                .withBalance(300L);

        Wallet subtractBalance = sourceWallet.subtractBalance(request.getAmount());
        Wallet addBalance = targetWallet.addBalance(request.getAmount());

        when(mockRepository.getByWalletId(eq(request.getSourceID())))
                .thenReturn(Response.createSuccessResponse(sourceWallet));

        Response<Void> response = walletService.transfer(request);
        assertFalse(response.isSuccess());
        List<Error> errors = response.getErrors();
        assertEquals(Errors.USER_BALANCE_IS_NOT_ENOUGH_ERROR_CODE, errors.get(0).getCode());
        assertEquals("amount", errors.get(0).getEntity());

        verify(mockRepository, times(1)).getByWalletId(anyString());
        verify(mockRepository, times(0)).updateBalance(eq(addBalance));
        verify(mockRepository, times(0)).updateBalance(eq(subtractBalance));
    }

    private ArgumentMatcher<Wallet> isValidWallet(CreateWalletRequest request) {
        return wallet -> wallet.getWalletID().equals(request.getWalletID())
                && wallet.getUserID().equals(request.getUserID())
                && wallet.getBalance().equals(0L);
    }
}