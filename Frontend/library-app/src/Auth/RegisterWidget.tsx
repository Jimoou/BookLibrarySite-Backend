/* eslint-disable react-hooks/exhaustive-deps */
import { Email, Key, Person } from "@mui/icons-material";
import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { DOMAIN, TOKEN } from "../lib/oktaConfig";

const RegistrationWidget = () => {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [emailAddress, setEmailAddress] = useState("");
  const [customEmailAddress, setCustomEmailAddress] = useState("");
  const [customEmailAddressSelected, setCustomEmailAddressSelected] =
    useState(false);
  const [password, setPassword] = useState("");
  const [pwError, setPwError] = useState("");
  const [displayWarning, setDisplayWarning] = useState(false);
  const { authState, oktaAuth } = useOktaAuth();
  useEffect(() => {
    async function authenticate() {
      if (!authState) return;

      if (authState.isAuthenticated) {
        navigate("/");
      }
    }
    authenticate();
  }, [authState, oktaAuth]);

  //비밀번호 조건
  const handlePasswordChange = (event: any) => {
    setPassword(event.target.value);
    const passwordRegex = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*])(?=.*[0-9]).{6,}$/;

    if (!passwordRegex.test(event.target.value)) {
      setPwError(
        "비밀번호는 영어, 숫자, 특수문자를 모두 포함하여 6자 이상이어야 합니다."
      );
    } else {
      setPwError("");
    }
  };

  const handleEmailAddressChange = (event: any) => {
    const selectedEmail = event.target.value;
    if (selectedEmail === "") {
      setCustomEmailAddressSelected(false);
    } else if (selectedEmail === "custom") {
      setCustomEmailAddressSelected(true);
    } else {
      setEmailAddress(selectedEmail);
      setCustomEmailAddressSelected(false);
    }
  };

  const handleCustomEmailChange = (event: any) => {
    setCustomEmailAddress(event.target.value);
    setEmailAddress(event.target.value);
  };

  const emailOptions = [
    "@gmail.com",
    "@naver.com",
    "@daum.net",
    "@hanmail.net",
    "@hotmail.com",
    "@nate.com",
    "@yahoo.co.kr",
    "@empas.com",
    "@dreamwiz.com",
    "@lycos.co.kr",
    "@korea.com",
    "@unitel.co.kr",
  ];
  const navigate = useNavigate();
  const register = async (event: any) => {
    event.preventDefault();
    const url = `${DOMAIN}/api/v1/users?activate=true`;
    const emailSet = email + emailAddress;
    const response = await fetch(url, {
      method: "POST",
      headers: {
        Authorization: `SSWS ${TOKEN}`,
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify({
        profile: {
          firstName: firstName,
          lastName: lastName,
          email: emailSet,
          login: emailSet,
        },
        credentials: {
          password: {
            value: password,
          },
        },
      }),
    });
    if (!response.ok) {
      setDisplayWarning(true);
      return;
    } else {
      navigate("/login");
    }
  };

  if (authState?.isAuthenticated) {
    return (
      <>
        <div>잘못된 접근입니다.</div>
      </>
    );
  }

  return (
    <div className="card col-md-4 m-auto mt-5">
      <form onSubmit={register}>
        <div className="card-body">
          <h4 className="card-title">회원가입</h4>
          <hr />
          {displayWarning && (
            <div className="alert alert-danger" role="alert">
              회원가입에 실패했습니다. 올바른 값을 입력하세요.
            </div>
          )}
          <div className="mb-3">
            <label htmlFor="inputName" className="form-label d-flex">
              <Person className="m-1" />
              <p className="m-1">
                이름 <span style={{ color: "red" }}>*</span>
              </p>
            </label>
            <div className="d-flex">
              <input
                id="inputName"
                type="text"
                className="form-control m-1"
                value={firstName}
                placeholder="성"
                onChange={(e) => setFirstName(e.target.value)}
                required
              />
              <input
                id="inputLastName"
                type="text"
                className="form-control m-1"
                value={lastName}
                placeholder="이름"
                onChange={(e) => setLastName(e.target.value)}
                required
              />
            </div>
          </div>
          <div className="mb-3">
            <label htmlFor="inputEmail" className="form-label d-flex">
              <Email className="m-1" />
              <p className="m-1">
                이메일 ID<span style={{ color: "red" }}>*</span>
              </p>
            </label>
            <div className="d-flex">
              <input
                id="inputEmail"
                className="form-control m-1"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
              <select
                className="form-select m-1"
                value={customEmailAddressSelected ? "custom" : emailAddress}
                onChange={handleEmailAddressChange}
                required
              >
                <option value="">이메일 선택</option>
                {emailOptions.map((option) => (
                  <option key={option} value={option}>
                    {option}
                  </option>
                ))}
                <option value="custom">직접 입력</option>
              </select>
              <input
                id="inputEmailCustom"
                type="email"
                className="form-control m-1"
                value={customEmailAddress}
                placeholder="직접 입력"
                onChange={handleCustomEmailChange}
                disabled={!customEmailAddressSelected}
              />
            </div>
          </div>
          <div className="mb-3">
            <label htmlFor="inputPassword" className="form-label d-flex">
              <Key className="m-1" />
              <p className="m-1">
                패스워드 <span style={{ color: "red" }}>*</span>
              </p>
            </label>
            <input
              id="inputPassword"
              type="password"
              className="form-control m-1"
              value={password}
              placeholder="패스워드를 입력하세요."
              onChange={handlePasswordChange}
              required
            />
            {pwError && <p style={{ color: "red" }}>{pwError}</p>}
          </div>
          <button className="btn btn-primary" data-testid="signin-button">
            회원가입
          </button>
        </div>
      </form>
    </div>
  );
};

export default RegistrationWidget;
