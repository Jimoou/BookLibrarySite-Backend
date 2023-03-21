import CartItemModel from "../../../models/CartItemModel";

export async function deleteBookInCart(id: number, authState: any) {
  const url = `${process.env.REACT_APP_API}/cart/secure/delete/item?id=${id}`;
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
  const url = `${process.env.REACT_APP_API}/cart/secure/add/item?bookId=${bookId}`;
  const paymentModel: CartItemModel = new CartItemModel(1, bookId);
  const requestOptions = {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(paymentModel),
  };
  const returnResponse = await fetch(url, requestOptions);
  if (!returnResponse.ok) {
    throw new Error("Something went wrong!");
  }
}
export async function increaseAmount(
  id: number,
  authState: any,
  amount: number
) {
  const url = `${process.env.REACT_APP_API}/cart/secure/increase/item/amount?id=${id}`;
  const paymentModel: CartItemModel = new CartItemModel(amount, id);
  const requestOptions = {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(paymentModel),
  };
  const returnResponse = await fetch(url, requestOptions);
  if (!returnResponse.ok) {
    throw new Error("Something went wrong!");
  }
}

export async function decreaseAmount(
  id: number,
  authState: any,
  amount: number
) {
  const url = `${process.env.REACT_APP_API}/cart/secure/decrease/item/amount?id=${id}`;
  const paymentModel: CartItemModel = new CartItemModel(amount, id);
  const requestOptions = {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(paymentModel),
  };
  const returnResponse = await fetch(url, requestOptions);
  if (!returnResponse.ok) {
    throw new Error("Something went wrong!");
  }
}
