class PurchaseModel {
  id?: number;
  userEmail: string;
  amount: number;
  book_id: number;

  constructor(userEmail: string, amount: number, book_id: number) {
    this.userEmail = userEmail;
    this.amount = amount;
    this.book_id = book_id;
  }
}
export default PurchaseModel;
