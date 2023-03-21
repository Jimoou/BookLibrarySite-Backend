class PendingPaymentRequest {
  bookId: number;
  amount: number;
  cartItemId: number;

  constructor(book_id: number, amount: number, cartItemId: number) {
    this.bookId = book_id;
    this.amount = amount;
    this.cartItemId = cartItemId;
  }
}
export default PendingPaymentRequest;
