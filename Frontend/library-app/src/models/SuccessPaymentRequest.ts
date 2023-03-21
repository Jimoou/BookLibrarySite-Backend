class SuccessPaymentRequest {
  paymentKey: string;
  orderId: string;
  paymentDate: string;
  status: string;
  totalAmount: number;
  constructor(
    paymentKey: string,
    orderId: string,
    paymentDate: string,
    status: string,
    totalAmount: number
  ) {
    this.paymentKey = paymentKey;
    this.orderId = orderId;
    this.paymentDate = paymentDate;
    this.status = status;
    this.totalAmount = totalAmount;
  }
}
export default SuccessPaymentRequest;
