import React, { useState, useEffect } from 'react';
import {
    Container,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Chip,
    CircularProgress,
} from '@mui/material';
import { getTransactions } from '../services/api';

const TransactionHistory = () => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);

    // Fetch transactions when component loads
    useEffect(() => {
        fetchTransactions();
    }, []);

    const fetchTransactions = async () => {
        try {
            const response = await getTransactions();
            setTransactions(response.data);
        } catch (error) {
            console.error('Error fetching transactions:', error);
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

    // Helper function to format date
    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString();
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
                Transaction History
            </Typography>

            {transactions.length === 0 ? (
                <Typography>No transactions found. Use "Load Test Data" to create sample transactions.</Typography>
            ) : (
                <>
                    <Typography variant="body1" sx={{ mb: 2 }}>
                        Total Transactions: {transactions.length}
                    </Typography>

                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Date</TableCell>
                                    <TableCell>Portfolio</TableCell>
                                    <TableCell>Type</TableCell>
                                    <TableCell>Stock</TableCell>
                                    <TableCell>Quantity</TableCell>
                                    <TableCell>Price</TableCell>
                                    <TableCell>Total</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {transactions.map((transaction) => (
                                    <TableRow key={transaction.transactionId}>
                                        <TableCell>
                                            {formatDate(transaction.transactionDate)}
                                        </TableCell>
                                        <TableCell>
                                            {transaction.portfolioName}
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label={transaction.transactionType}
                                                color={transaction.transactionType === 'BUY' ? 'primary' : 'secondary'}
                                                size="small"
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <div>
                                                <strong>{transaction.stockSymbol}</strong>
                                                <br />
                                                <small>{transaction.stockName}</small>
                                            </div>
                                        </TableCell>
                                        <TableCell>
                                            {transaction.quantity}
                                        </TableCell>
                                        <TableCell>
                                            {formatMoney(transaction.pricePerShare)}
                                        </TableCell>
                                        <TableCell>
                                            {formatMoney(transaction.totalAmount)}
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </>
            )}
        </Container>
    );
};

export default TransactionHistory;