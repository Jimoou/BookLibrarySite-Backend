import { NavLink } from "react-router-dom";
import { useOktaAuth } from "@okta/okta-react";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import {
  AccountBalance,
  AdminPanelSettings,
  AllInbox,
  Home,
  Login,
  Logout,
  PersonAdd,
  Search,
  ShoppingCart,
} from "@mui/icons-material";
export const Navbar = () => {
  const { oktaAuth, authState } = useOktaAuth();

  if (!authState) {
    return <SpinnerLoading />;
  }

  const handleLogout = async () => oktaAuth.signOut();

  return (
    <nav
      className="navbar navbar-expand-lg py-3"
      style={{ borderBottom: "1px solid gainsboro" }}
    >
      <div className="container-fluid">
        <NavLink className="navbar-brand" to="/">
          <AccountBalance />
          스프링 도서관
        </NavLink>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNavDropdown"
          aria-controls="navbarNavDropdown"
          aria-expanded="false"
          aria-label="Toggle Navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNavDropdown">
          <ul className="navbar-nav">
            <li className="nav-item">
              <NavLink className="nav-link" to="/">
                <Home />
                &nbsp;홈
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/search">
                <Search />
                &nbsp;책 찾기
              </NavLink>
            </li>
            {authState.isAuthenticated &&
              authState.accessToken?.claims?.userType === "admin" && (
                <li className="nav-item">
                  <NavLink className="nav-link" to="/admin">
                    <AdminPanelSettings />
                    &nbsp;관리자 페이지
                  </NavLink>
                </li>
              )}
          </ul>
          <ul className="navbar-nav ms-auto">
            {authState.isAuthenticated ? (
              <li className="nav-item">
                <NavLink className="nav-link" to="/shelf">
                  <AllInbox />
                  &nbsp;내 서랍
                </NavLink>
              </li>
            ) : (
              <li className="nav-item">
                <NavLink className="nav-link" to="/login">
                  <AllInbox />
                  &nbsp;내 서랍
                </NavLink>
              </li>
            )}
            {authState.isAuthenticated ? (
              <li className="nav-item">
                <NavLink className="nav-link" to="/cart">
                  <ShoppingCart />
                  &nbsp;장바구니
                </NavLink>
              </li>
            ) : (
              <li className="nav-item">
                <NavLink className="nav-link" to="/login">
                  <ShoppingCart />
                  &nbsp;장바구니
                </NavLink>
              </li>
            )}

            {!authState.isAuthenticated ? (
              <li className="nav-item">
                <NavLink className="nav-link" to="/login">
                  <Login />
                  &nbsp;로그인
                </NavLink>
              </li>
            ) : (
              <li className="nav-item">
                <span className="nav-link" onClick={handleLogout}>
                  <Logout />
                  &nbsp;로그아웃
                </span>
              </li>
            )}
            {!authState.isAuthenticated && (
              <li className="nav-item">
                <NavLink className="nav-link" to="/register">
                  <PersonAdd />
                  &nbsp;회원가입
                </NavLink>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};
