class PaymentHistory {
  paymentKey: string;
  orderName: string;
  totalPrice: number;
  paymentDate: string;
  orderId: string;
  constructor(
    paymentKey: string,
    oderName: string,
    totalPrice: number,
    paymentDate: string,
    orderId: string
  ) {
    this.paymentKey = paymentKey;
    this.orderName = oderName;
    this.totalPrice = totalPrice;
    this.paymentDate = paymentDate;
    this.orderId = orderId;
  }
}
export default PaymentHistory;
