class SuccessPurchaseRequest {
  paymentKey: string;
  orderId: string;
  purchaseDate: string;
  status: string;
  totalAmount: number;
  constructor(
    paymentKey: string,
    orderId: string,
    purchaseDate: string,
    status: string,
    totalAmount: number
  ) {
    this.paymentKey = paymentKey;
    this.orderId = orderId;
    this.purchaseDate = purchaseDate;
    this.status = status;
    this.totalAmount = totalAmount;
  }
}
export default SuccessPurchaseRequest;
