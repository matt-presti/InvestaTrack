import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { loadTestData, clearTestData } from '../services/api';

const Navbar = () => {
    const navigate = useNavigate();

    const handleLoadData = async () => {
        try {
            await loadTestData();
            alert('Test data loaded!');
            window.location.reload();
        } catch (error) {
            alert('Error loading data');
        }
    };

    const handleClearData = async () => {
        try {
            await clearTestData();
            alert('Data cleared!');
            window.location.reload();
        } catch (error) {
            alert('Error clearing data');
        }
    };

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" sx={{ flexGrow: 1, cursor: 'pointer' }}
                            onClick={() => navigate('/')}>
                    InvestaTrack
                </Typography>

                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button color="inherit" onClick={() => navigate('/')}>
                        Dashboard
                    </Button>

                    <Button color="inherit" onClick={() => navigate('/transactions')}>
                        Transactions
                    </Button>

                    <Button color="inherit" onClick={() => navigate('/add-transaction')}>
                        Add Transaction
                    </Button>

                    <Button color="inherit" onClick={handleLoadData}>
                        Load Test Data
                    </Button>

                    <Button color="inherit" onClick={handleClearData}>
                        Clear Data
                    </Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Navbar;