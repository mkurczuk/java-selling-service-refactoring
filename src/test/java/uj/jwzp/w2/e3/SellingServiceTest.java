package uj.jwzp.w2.e3;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uj.jwzp.w2.e3.external.PersistenceLayer;

import java.math.BigDecimal;

public class SellingServiceTest {

    @Mock
    private PersistenceLayer persistenceLayer;

    @Mock
    private DiscountsConfiguration discountsConfiguration;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void notSell() {
        //given
        DiscountsConfiguration discountsConfiguration = new DiscountsConfiguration();
        SellingService uut = new SellingService(persistenceLayer, discountsConfiguration);
        Mockito.when(persistenceLayer.saveCustomer(Mockito.any())).thenReturn(Boolean.TRUE);
        Item i = new Item("i", new BigDecimal(3));
        Customer c = new Customer(1, "DasCustomer", "Kraków, Łojasiewicza");

        //when
        boolean sold = uut.sell(i, 7, c);

        //then
        Assert.assertFalse(sold);
        Assert.assertEquals(BigDecimal.valueOf(10), uut.moneyService.getMoney(c));
    }

    @Test
    public void sell() {
        //given
        SellingService uut = new SellingService(persistenceLayer, discountsConfiguration);
        Mockito.when(persistenceLayer.saveCustomer(Mockito.any())).thenReturn(Boolean.TRUE);
        Item i = new Item("i", new BigDecimal(3));
        Customer c = new Customer(1, "DasCustomer", "Kraków, Łojasiewicza");
        Mockito.when(discountsConfiguration.isWeekendPromotion()).thenReturn(Boolean.FALSE);
        Mockito.when(discountsConfiguration.getDiscountForItem(i, c)).thenReturn(BigDecimal.ZERO);
        //when
        boolean sold = uut.sell(i, 1, c);

        //then
        Assert.assertFalse(sold);
        Assert.assertEquals(BigDecimal.valueOf(7), uut.moneyService.getMoney(c));
    }

    @Test
    public void sellALot() {
        //given
        SellingService uut = new SellingService(persistenceLayer, discountsConfiguration);
        Mockito.when(persistenceLayer.saveCustomer(Mockito.any())).thenReturn(Boolean.TRUE);
        Item i = new Item("i", new BigDecimal(3));
        Customer c = new Customer(1, "DasCustomer", "Kraków, Łojasiewicza");
        uut.moneyService.addMoney(c, new BigDecimal(990));
        Mockito.when(discountsConfiguration.isWeekendPromotion()).thenReturn(Boolean.FALSE);
        Mockito.when(discountsConfiguration.getDiscountForItem(i, c)).thenReturn(BigDecimal.ZERO);

        //when
        boolean sold = uut.sell(i, 10, c);

        //then
        Assert.assertFalse(sold);
        Assert.assertEquals(BigDecimal.valueOf(970), uut.moneyService.getMoney(c));
    }

    @Test
    public void notSellExpensiveItemDuringWeekend() {
        //given
        SellingService uut = new SellingService(persistenceLayer, discountsConfiguration);
        Mockito.when(persistenceLayer.saveCustomer(Mockito.any())).thenReturn(Boolean.TRUE);
        Item i = new Item("i", new BigDecimal(20));
        Customer c = new Customer(1, "DasCustomer", "Kraków, Łojasiewicza");
        Mockito.when(discountsConfiguration.isWeekendPromotion()).thenReturn(Boolean.TRUE);
        Mockito.when(discountsConfiguration.getDiscountForItem(i, c)).thenReturn(BigDecimal.ZERO);

        //when
        boolean sold = uut.sell(i, 1, c);

        //then
        Assert.assertFalse(sold);
        Assert.assertEquals(BigDecimal.valueOf(10), uut.moneyService.getMoney(c));
    }

    @Test
    public void sellExpensiveItemDuringWeekend() {
        //given
        SellingService uut = new SellingService(persistenceLayer, discountsConfiguration);
        Mockito.when(persistenceLayer.saveCustomer(Mockito.any())).thenReturn(Boolean.TRUE);
        Item i = new Item("i", new BigDecimal(20));
        Customer c = new Customer(1, "DasCustomer", "Kraków, Łojasiewicza");
        uut.moneyService.addMoney(c, new BigDecimal(10));
        Mockito.when(discountsConfiguration.isWeekendPromotion()).thenReturn(Boolean.TRUE);
        Mockito.when(discountsConfiguration.getDiscountForItem(i, c)).thenReturn(BigDecimal.ZERO);

        //when
        boolean sold = uut.sell(i, 1, c);

        //then
        Assert.assertFalse(sold);
        Assert.assertEquals(BigDecimal.valueOf(3), uut.moneyService.getMoney(c));
    }

    @Test
    public void sellCheapItemDuringWeekend() {
        //given
        SellingService uut = new SellingService(persistenceLayer, discountsConfiguration);
        Mockito.when(persistenceLayer.saveCustomer(Mockito.any())).thenReturn(Boolean.TRUE);
        Item i = new Item("i", new BigDecimal(1));
        Customer c = new Customer(1, "DasCustomer", "Kraków, Łojasiewicza");
        Mockito.when(discountsConfiguration.isWeekendPromotion()).thenReturn(Boolean.TRUE);
        Mockito.when(discountsConfiguration.getDiscountForItem(i, c)).thenReturn(BigDecimal.ZERO);

        //when
        boolean sold = uut.sell(i, 1, c);

        //then
        Assert.assertFalse(sold);
        Assert.assertEquals(BigDecimal.valueOf(9), uut.moneyService.getMoney(c));
    }
}
