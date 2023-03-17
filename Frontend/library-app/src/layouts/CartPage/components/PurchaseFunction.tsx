import PurchaseModel from "../../../models/CartItemModel";

export async function deleteBookInCart(bookId: number, authState: any) {
  const url = `${process.env.REACT_APP_API}/cart/secure/delete/book?bookId=${bookId}`;
  const requestOptions = {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
      "Content-Type": "application/json",
    },
  };
  const returnResponse = await fetch(url, requestOptions);
  if (!returnResponse.ok) {
    throw new Error("Something went wrong!");
  }
}
export async function addBookInCart(bookId: number, authState: any) {
  const url = `${process.env.REACT_APP_API}/cart/secure/add/book?bookId=${bookId}`;
  const purchaseModel: PurchaseModel = new PurchaseModel(1, bookId);
  const requestOptions = {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(purchaseModel),
  };
  const returnResponse = await fetch(url, requestOptions);
  if (!returnResponse.ok) {
    throw new Error("Something went wrong!");
  }
}
export async function increaseAmount(
  bookId: number,
  authState: any,
  amount: number
) {
  const url = `${process.env.REACT_APP_API}/cart/secure/increase/book/amount?bookId=${bookId}`;
  const purchaseModel: PurchaseModel = new PurchaseModel(amount, bookId);
  const requestOptions = {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(purchaseModel),
  };
  const returnResponse = await fetch(url, requestOptions);
  if (!returnResponse.ok) {
    throw new Error("Something went wrong!");
  }
}

export async function decreaseAmount(
  bookId: number,
  authState: any,
  amount: number
) {
  const url = `${process.env.REACT_APP_API}/cart/secure/decrease/book/amount?bookId=${bookId}`;
  const purchaseModel: PurchaseModel = new PurchaseModel(amount, bookId);
  const requestOptions = {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(purchaseModel),
  };
  const returnResponse = await fetch(url, requestOptions);
  if (!returnResponse.ok) {
    throw new Error("Something went wrong!");
  }
}
