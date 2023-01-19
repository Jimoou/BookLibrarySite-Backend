import { NavLink } from "react-router-dom";

export const Navbar = () => {
  return (
    <nav className='navbar navbar-expand-lg bg-light main-color py-3'>
      <div className='container-fluid'>
        <span className='navbar-brand'>Jiwoon to Read</span>
        <button
          className='navbar-toggler'
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
                Home
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink className="nav-link" to="/search">
                Search Books
              </NavLink>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#!">
                Shelf
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#!">
                Admin
              </a>
            </li>
          </ul>
          <ul className="navbar-nav ms-auto">
            <li className="nav-item m-1">
              <a type="button" className="btn btn-outline-secondary" href="#!">
                Sign in
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};
