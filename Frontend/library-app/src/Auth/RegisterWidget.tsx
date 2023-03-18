/* eslint-disable react-hooks/exhaustive-deps */
import { Email, Key, Person } from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Grid,
  TextField,
  Typography,
  Alert,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  Grow,
} from "@mui/material";
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
    <Container maxWidth="sm" sx={{ mt: 4 }}>
      <Grow in>
        <Card>
          <CardContent>
            <form onSubmit={register}>
              <Typography variant="h4" align="center">
                회원가입
              </Typography>
              <hr />
              {displayWarning && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  회원가입에 실패했습니다. 올바른 값을 입력하세요.
                </Alert>
              )}

              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <InputLabel htmlFor="inputName">
                    <Person sx={{ mr: 1 }} />
                    이름 <span style={{ color: "red" }}>*</span>
                  </InputLabel>
                  <Grid container spacing={1}>
                    <Grid item xs={6}>
                      <TextField
                        id="inputName"
                        value={firstName}
                        placeholder="성"
                        onChange={(e) => setFirstName(e.target.value)}
                        fullWidth
                        required
                      />
                    </Grid>
                    <Grid item xs={6}>
                      <TextField
                        id="inputLastName"
                        value={lastName}
                        placeholder="이름"
                        onChange={(e) => setLastName(e.target.value)}
                        fullWidth
                        required
                      />
                    </Grid>
                  </Grid>
                </Grid>

                <Grid item xs={12}>
                  <InputLabel htmlFor="inputEmail">
                    <Email sx={{ mr: 1 }} />
                    이메일 ID<span style={{ color: "red" }}>*</span>
                  </InputLabel>
                  <Grid container spacing={1}>
                    <Grid item xs={6}>
                      <TextField
                        id="inputEmail"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        fullWidth
                      />
                    </Grid>
                    <Grid item xs={6}>
                      <FormControl fullWidth>
                        <Select
                          id="emailSelect"
                          value={
                            customEmailAddressSelected ? "custom" : emailAddress
                          }
                          onChange={handleEmailAddressChange}
                          required
                        >
                          <MenuItem value="">
                            <em>이메일 선택</em>
                          </MenuItem>
                          {emailOptions.map((option) => (
                            <MenuItem key={option} value={option}>
                              {option}
                            </MenuItem>
                          ))}
                          <MenuItem value="custom">직접 입력</MenuItem>
                        </Select>
                      </FormControl>
                    </Grid>
                    <Grid item xs={12}>
                      <TextField
                        id="inputEmailCustom"
                        type="email"
                        value={customEmailAddress}
                        placeholder="직접 입력"
                        onChange={handleCustomEmailChange}
                        fullWidth
                        disabled={!customEmailAddressSelected}
                      />
                    </Grid>
                  </Grid>
                </Grid>

                <Grid item xs={12}>
                  <InputLabel htmlFor="inputPassword">
                    <Key sx={{ mr: 1 }} />
                    패스워드 <span style={{ color: "red" }}>*</span>
                  </InputLabel>
                  <TextField
                    id="inputPassword"
                    type="password"
                    value={password}
                    placeholder="패스워드를 입력하세요."
                    onChange={handlePasswordChange}
                    fullWidth
                    required
                  />
                  {pwError && (
                    <Typography color="error" variant="body2">
                      {pwError}
                    </Typography>
                  )}
                </Grid>
              </Grid>
              <Box sx={{ mt: 3 }}>
                <Button
                  variant="contained"
                  color="primary"
                  type="submit"
                  fullWidth
                  data-testid="signin-button"
                  disabled={pwError !== ""}
                >
                  회원가입
                </Button>
              </Box>
            </form>
          </CardContent>
        </Card>
      </Grow>
    </Container>
  );
};

export default RegistrationWidget;
