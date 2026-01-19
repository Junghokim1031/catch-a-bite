import { Navigate, Route, Routes } from "react-router-dom";
import OwnerMainPlaceholder from "../pages/OwnerMainPlaceholder.jsx";
import RiderMainPlaceholder from "../pages/RiderMainPlaceholder.jsx";
import RoleLoginPage from "../pages/RoleLoginPage.jsx";
import RoleSelectPage from "../pages/RoleSelectPage.jsx";
import SignupOwnerPage from "../pages/SignupOwnerPage.jsx";
import SignupRiderPage from "../pages/SignupRiderPage.jsx";
import SignupUserPage from "../pages/SignupUserPage.jsx";
import UserMainPlaceholder from "../pages/UserMainPlaceholder.jsx";

export default function AppRouter({ onAuthRefresh }) {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/select" replace />} />
      <Route path="/select" element={<RoleSelectPage />} />

      <Route
        path="/user/login"
        element={<RoleLoginPage role="USER" onAuthRefresh={onAuthRefresh} />}
      />
      <Route
        path="/owner/login"
        element={<RoleLoginPage role="OWNER" onAuthRefresh={onAuthRefresh} />}
      />
      <Route
        path="/rider/login"
        element={<RoleLoginPage role="RIDER" onAuthRefresh={onAuthRefresh} />}
      />

      <Route path="/user/signup" element={<SignupUserPage />} />
      <Route path="/owner/signup" element={<SignupOwnerPage />} />
      <Route path="/rider/signup" element={<SignupRiderPage />} />

      <Route path="/user" element={<UserMainPlaceholder />} />
      <Route path="/owner" element={<OwnerMainPlaceholder />} />
      <Route path="/rider" element={<RiderMainPlaceholder />} />

      <Route path="*" element={<Navigate to="/select" replace />} />
    </Routes>
  );
}
