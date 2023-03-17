class AddPurchaseHistoryRequest {
  bookId: number;
  amount: number;

  constructor(book_id: number, amount: number) {
    this.bookId = book_id;
    this.amount = amount;
  }
}
export default AddPurchaseHistoryRequest;
