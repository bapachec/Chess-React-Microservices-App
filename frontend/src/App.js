//library components
import { Container } from "react-bootstrap";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
//self made components
import Header from "./components/Header";

//pages
import PlayPage from "./pages/PlayPage";
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/RegisterPage";
import HomePage from "./pages/HomePage";
import UserProvider from "./contexts/UserProvider";
import GameProvider from "./contexts/GameProvider";
import ApiProvider from "./contexts/ApiProvider";

export default function App() {
  
  return (
    <Container fluid className="App">
      <BrowserRouter>
        <ApiProvider>
          <UserProvider>
              <Header />
                <Routes>
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="/sign-up" element={<SignupPage/>} />
                  <Route path="/" element={<HomePage/>} />
                    <Route path="*" element={
                      <GameProvider>
                        <Routes>
                          <Route path="/play" element={<PlayPage />} />
                          <Route path="*" element={<Navigate to="/" />} />
                        </Routes>
                      </GameProvider>
                    } />
                </Routes>
            </UserProvider>
        </ApiProvider>
      </BrowserRouter>
    </Container>
  );

}