class SuccessPaymentRequest {
  paymentKey: string;
  orderId: string;
  amount: number;
  constructor(paymentKey: string, orderId: string, amount: number) {
    this.paymentKey = paymentKey;
    this.orderId = orderId;
    this.amount = amount;
  }
}
export default SuccessPaymentRequest;
