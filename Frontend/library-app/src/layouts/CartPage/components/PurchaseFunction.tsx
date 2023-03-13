import PurchaseModel from "../../../models/PurchaseModel";

export async function deleteBookInCart(bookId: number, authState: any) {
  const url = `${process.env.REACT_APP_API}/books/secure/delete/bookincart/?bookId=${bookId}`;
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
export async function addBookInCart(
  bookId: number,
  authState: any,
  userEmail: string
) {
  const url = `${process.env.REACT_APP_API}/books/secure/add/bookincart/?bookId=${bookId}`;
  const purchaseModel: PurchaseModel = new PurchaseModel(userEmail, 1, bookId);
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
