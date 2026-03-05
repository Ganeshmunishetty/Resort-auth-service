import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Paper,
  Typography,
  Box,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Alert,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormLabel,
  Grid,
  Tabs,
  Tab
} from '@mui/material';
import {
  CheckCircle as ApproveIcon,
  Cancel as RejectIcon,
  LockOpen as UnlockIcon,
  PersonAdd as AddAdminIcon
} from '@mui/icons-material';
import {
  getPendingUsers,
  getAllUsers,
  approveUser,
  rejectUser,
  unlockAccount,
  registerAdmin
} from '../services/api';

const COUNTRY_CODES = [
  { code: '+1', name: 'USA/Canada' },
  { code: '+44', name: 'UK' },
  { code: '+91', name: 'India' },
  { code: '+86', name: 'China' },
  { code: '+81', name: 'Japan' },
  { code: '+49', name: 'Germany' },
  { code: '+33', name: 'France' },
  { code: '+61', name: 'Australia' },
  { code: '+971', name: 'UAE' },
  { code: '+65', name: 'Singapore' }
];

function AdminDashboard() {
  const navigate = useNavigate();
  const [pendingUsers, setPendingUsers] = useState([]);
  const [allUsers, setAllUsers] = useState([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [tabValue, setTabValue] = useState(0);
  const [openAdminDialog, setOpenAdminDialog] = useState(false);
  const [adminFormData, setAdminFormData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    age: '',
    email: '',
    countryCode: '+1',
    phoneNumber: '',
    gender: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [pending, all] = await Promise.all([
        getPendingUsers(),
        getAllUsers()
      ]);
      setPendingUsers(pending.data);
      setAllUsers(all.data);
    } catch (err) {
      setError('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (userId) => {
    try {
      await approveUser(userId);
      setSuccess('User approved successfully');
      loadData();
    } catch (err) {
      setError('Failed to approve user');
    }
  };

  const handleReject = async (userId) => {
    try {
      await rejectUser(userId);
      setSuccess('User rejected successfully');
      loadData();
    } catch (err) {
      setError('Failed to reject user');
    }
  };

  const handleUnlock = async (userId) => {
    try {
      await unlockAccount(userId);
      setSuccess('Account unlocked successfully');
      loadData();
    } catch (err) {
      setError('Failed to unlock account');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    navigate('/login');
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
    setError('');
    setSuccess('');
  };

  const handleOpenAdminDialog = () => {
    setOpenAdminDialog(true);
    setError('');
    setSuccess('');
  };

  const handleCloseAdminDialog = () => {
    setOpenAdminDialog(false);
    setAdminFormData({
      username: '',
      password: '',
      confirmPassword: '',
      age: '',
      email: '',
      countryCode: '+1',
      phoneNumber: '',
      gender: ''
    });
  };

  const handleAdminFormChange = (e) => {
    setAdminFormData({
      ...adminFormData,
      [e.target.name]: e.target.value
    });
  };

  const validateAdminForm = () => {
    if (!adminFormData.username || !adminFormData.password || !adminFormData.confirmPassword ||
        !adminFormData.age || !adminFormData.email || !adminFormData.phoneNumber || !adminFormData.gender) {
      setError('All fields are required');
      return false;
    }

    if (adminFormData.password !== adminFormData.confirmPassword) {
      setError('Passwords do not match');
      return false;
    }

    if (adminFormData.password.length < 6) {
      setError('Password must be at least 6 characters');
      return false;
    }

    if (parseInt(adminFormData.age) < 18) {
      setError('Age must be at least 18');
      return false;
    }

    if (!/^[0-9]{10}$/.test(adminFormData.phoneNumber)) {
      setError('Phone number must be exactly 10 digits');
      return false;
    }

    return true;
  };

  const handleCreateAdmin = async () => {
    setError('');
    setSuccess('');

    if (!validateAdminForm()) {
      return;
    }

    try {
      await registerAdmin(adminFormData);
      setSuccess('Admin account created successfully!');
      handleCloseAdminDialog();
      loadData();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create admin account');
    }
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ mt: 4, mb: 4 }}>
        <Paper elevation={3} sx={{ p: 4 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Typography variant="h4" component="h1">
              Admin Dashboard
            </Typography>
            <Box>
              <Button
                variant="contained"
                color="secondary"
                startIcon={<AddAdminIcon />}
                onClick={handleOpenAdminDialog}
                sx={{ mr: 2 }}
              >
                Create Admin
              </Button>
              <Button variant="outlined" onClick={handleLogout}>
                Logout
              </Button>
            </Box>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError('')}>
              {error}
            </Alert>
          )}

          {success && (
            <Alert severity="success" sx={{ mb: 2 }} onClose={() => setSuccess('')}>
              {success}
            </Alert>
          )}

          <Tabs value={tabValue} onChange={handleTabChange} sx={{ mb: 3 }}>
            <Tab label={`Pending Approvals (${pendingUsers.length})`} />
            <Tab label={`All Users (${allUsers.length})`} />
          </Tabs>

          {tabValue === 0 && (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Username</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Phone</TableCell>
                    <TableCell>Role</TableCell>
                    <TableCell>Age</TableCell>
                    <TableCell>Gender</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {pendingUsers.length === 0 ? (
                    <TableRow>
                      <TableCell colSpan={7} align="center">
                        No pending approvals
                      </TableCell>
                    </TableRow>
                  ) : (
                    pendingUsers.map((user) => (
                      <TableRow key={user.id}>
                        <TableCell>{user.username}</TableCell>
                        <TableCell>{user.email}</TableCell>
                        <TableCell>{user.countryCode} {user.phoneNumber}</TableCell>
                        <TableCell>
                          <Chip label={user.role} color="primary" size="small" />
                        </TableCell>
                        <TableCell>{user.age}</TableCell>
                        <TableCell>{user.gender}</TableCell>
                        <TableCell>
                          <IconButton
                            color="success"
                            onClick={() => handleApprove(user.id)}
                            title="Approve"
                          >
                            <ApproveIcon />
                          </IconButton>
                          <IconButton
                            color="error"
                            onClick={() => handleReject(user.id)}
                            title="Reject"
                          >
                            <RejectIcon />
                          </IconButton>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}

          {tabValue === 1 && (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Username</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Phone</TableCell>
                    <TableCell>Role</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Locked</TableCell>
                    <TableCell>Actions</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {allUsers.map((user) => (
                    <TableRow key={user.id}>
                      <TableCell>{user.username}</TableCell>
                      <TableCell>{user.email}</TableCell>
                      <TableCell>{user.countryCode} {user.phoneNumber}</TableCell>
                      <TableCell>
                        <Chip label={user.role} color="primary" size="small" />
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={user.status}
                          color={user.status === 'APPROVED' ? 'success' : user.status === 'PENDING' ? 'warning' : 'error'}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        {user.accountLocked ? (
                          <Chip label="Locked" color="error" size="small" />
                        ) : (
                          <Chip label="Active" color="success" size="small" />
                        )}
                      </TableCell>
                      <TableCell>
                        {user.accountLocked && (
                          <IconButton
                            color="primary"
                            onClick={() => handleUnlock(user.id)}
                            title="Unlock Account"
                          >
                            <UnlockIcon />
                          </IconButton>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Paper>
      </Box>

      {/* Create Admin Dialog */}
      <Dialog open={openAdminDialog} onClose={handleCloseAdminDialog} maxWidth="sm" fullWidth>
        <DialogTitle>Create New Admin Account</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2 }}>
            <TextField
              fullWidth
              label="Username"
              name="username"
              value={adminFormData.username}
              onChange={handleAdminFormChange}
              margin="normal"
              required
            />

            <TextField
              fullWidth
              label="Email"
              name="email"
              type="email"
              value={adminFormData.email}
              onChange={handleAdminFormChange}
              margin="normal"
              required
            />

            <TextField
              fullWidth
              label="Password"
              name="password"
              type="password"
              value={adminFormData.password}
              onChange={handleAdminFormChange}
              margin="normal"
              required
            />

            <TextField
              fullWidth
              label="Confirm Password"
              name="confirmPassword"
              type="password"
              value={adminFormData.confirmPassword}
              onChange={handleAdminFormChange}
              margin="normal"
              required
            />

            <TextField
              fullWidth
              label="Age"
              name="age"
              type="number"
              value={adminFormData.age}
              onChange={handleAdminFormChange}
              margin="normal"
              required
              inputProps={{ min: 18 }}
            />

            <Grid container spacing={2} sx={{ mt: 1 }}>
              <Grid item xs={4}>
                <FormControl fullWidth>
                  <InputLabel>Country Code</InputLabel>
                  <Select
                    name="countryCode"
                    value={adminFormData.countryCode}
                    onChange={handleAdminFormChange}
                    label="Country Code"
                    required
                  >
                    {COUNTRY_CODES.map((country) => (
                      <MenuItem key={country.code} value={country.code}>
                        {country.code}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={8}>
                <TextField
                  fullWidth
                  label="Phone Number"
                  name="phoneNumber"
                  value={adminFormData.phoneNumber}
                  onChange={handleAdminFormChange}
                  required
                  placeholder="10 digits"
                  inputProps={{ maxLength: 10 }}
                />
              </Grid>
            </Grid>

            <FormControl fullWidth component="fieldset" sx={{ mt: 2 }}>
              <FormLabel component="legend">Gender</FormLabel>
              <RadioGroup
                row
                name="gender"
                value={adminFormData.gender}
                onChange={handleAdminFormChange}
              >
                <FormControlLabel value="Male" control={<Radio />} label="Male" />
                <FormControlLabel value="Female" control={<Radio />} label="Female" />
                <FormControlLabel value="Other" control={<Radio />} label="Other" />
              </RadioGroup>
            </FormControl>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseAdminDialog}>Cancel</Button>
          <Button onClick={handleCreateAdmin} variant="contained" color="primary">
            Create Admin
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

export default AdminDashboard;
