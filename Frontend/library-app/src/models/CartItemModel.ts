class CartItemModel {
  id?: number;
  userEmail?: string;
  amount: number;
  book_id: number;

  constructor(amount: number, book_id: number) {
    this.amount = amount;
    this.book_id = book_id;
  }
}
export default CartItemModel;
