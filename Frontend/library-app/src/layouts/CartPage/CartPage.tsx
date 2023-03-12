import { loadPaymentWidget, ANONYMOUS } from "@tosspayments/payment-widget-sdk";
import { useState, useEffect } from "react";
import { tossConfig } from "../../lib/tossConfig";

export const CartPage = () => {
  const [totalPrice, setTotalPrice] = useState(0);

  const tossPay = async () => {
    const paymentWidget = await loadPaymentWidget(
      tossConfig.clientKey,
      tossConfig.customerKey
    ); // 회원 결제
    paymentWidget.renderPaymentMethods("#payment-method", totalPrice);

    paymentWidget.requestPayment({
      orderId: "AD8aZDpbzXs4EQa-UkIX6",
      orderName: "토스 티셔츠 외 2건",
      successUrl: "https://localhost:8443/success",
      failUrl: "https://localhost:8443/fail",
      customerEmail: "customer123@gmail.com",
      customerName: "김토스",
    });
  };

  return (
    <div>
      <div id="payment-method"></div>
      <button onClick={tossPay}>결제하기</button>
    </div>
  );
};

export default CartPage;
