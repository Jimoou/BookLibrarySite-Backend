class PurchaseHistory {
  paymentKey: string;
  orderName: string;
  totalPrice: number;
  purchaseDate: string;
  orderId: string;
  constructor(
    paymentKey: string,
    oderName: string,
    totalPrice: number,
    purchaseDate: string,
    orderId: string
  ) {
    this.paymentKey = paymentKey;
    this.orderName = oderName;
    this.totalPrice = totalPrice;
    this.purchaseDate = purchaseDate;
    this.orderId = orderId;
  }
}
export default PurchaseHistory;
