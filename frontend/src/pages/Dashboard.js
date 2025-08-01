import React, { useState, useEffect } from 'react';
import { Container, Typography, Grid, Card, CardContent, CircularProgress } from '@mui/material';
import { getPortfolios } from '../services/api';

const Dashboard = () => {
    const [portfolios, setPortfolios] = useState([]);
    const [loading, setLoading] = useState(true);

    // Fetch portfolios when component loads
    useEffect(() => {
        fetchPortfolios();
    }, []);

    const fetchPortfolios = async () => {
        try {
            const response = await getPortfolios();
            setPortfolios(response.data);
        } catch (error) {
            console.error('Error fetching portfolios:', error);
        } finally {
            setLoading(false);
        }
    };

    // Helper function to format money
    const formatMoney = (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD',
        }).format(amount);
    };

    if (loading) {
        return (
            <Container sx={{ mt: 4, textAlign: 'center' }}>
                <CircularProgress />
            </Container>
        );
    }

    return (
        <Container sx={{ mt: 4 }}>
            <Typography variant="h4" gutterBottom>
                My Portfolios
            </Typography>

            {portfolios.length === 0 ? (
                <Typography>No portfolios found. Click "Load Test Data" to get started!</Typography>
            ) : (
                <Grid container spacing={3}>
                    {portfolios.map((portfolio) => (
                        <Grid item xs={12} md={6} key={portfolio.portfolioID}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6" gutterBottom>
                                        {portfolio.name}
                                    </Typography>

                                    <Typography color="text.secondary" gutterBottom>
                                        {portfolio.description}
                                    </Typography>

                                    <Typography variant="h5" sx={{ mt: 2 }}>
                                        {formatMoney(portfolio.totalValue)}
                                    </Typography>

                                    <Typography color="text.secondary">
                                        Total Value
                                    </Typography>

                                    <Typography sx={{ mt: 1 }}>
                                        Cost: {formatMoney(portfolio.totalCost)}
                                    </Typography>

                                    <Typography
                                        color={portfolio.gainLoss >= 0 ? 'success.main' : 'error.main'}
                                        sx={{ fontWeight: 'bold' }}
                                    >
                                        {portfolio.gainLoss >= 0 ? '+' : ''}{formatMoney(portfolio.gainLoss)}
                                    </Typography>

                                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                                        Owner: {portfolio.user?.fullName}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}
        </Container>
    );
};

export default Dashboard;