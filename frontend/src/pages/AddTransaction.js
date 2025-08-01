import React, { useState, useEffect } from 'react';
import {
    Container,
    Typography,
    Paper,
    TextField,
    Button,
    MenuItem,
    Box,
    Alert,
} from '@mui/material';
import { getPortfolios, getStocks, createBuyTransaction, createSellTransaction } from '../services/api';

const AddTransaction = () => {
    const [portfolios, setPortfolios] = useState([]);
    const [stocks, setStocks] = useState([]);
    const [message, setMessage] = useState('');

    // Form data
    const [portfolioId, setPortfolioId] = useState('');
    const [stockId, setStockId] = useState('');
    const [transactionType, setTransactionType] = useState('BUY');
    const [quantity, setQuantity] = useState('');
    const [price, setPrice] = useState('');
    const [fees, setFees] = useState('0');

    // Load portfolios and stocks when component starts
    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const [portfoliosRes, stocksRes] = await Promise.all([
                getPortfolios(),
                getStocks()
            ]);
            setPortfolios(portfoliosRes.data);
            setStocks(stocksRes.data);
        } catch (error) {
            console.error('Error loading data:', error);
            setMessage('Error loading data. Make sure backend is running.');
        }
    };

    // When stock changes, update price automatically
    const handleStockChange = (stockId) => {
        setStockId(stockId);
        const selectedStock = stocks.find(s => s.stockID.toString() === stockId);
        if (selectedStock) {
            setPrice(selectedStock.currentPrice.toString());
        }
    };

    // Submit the form
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!portfolioId || !stockId || !quantity || !price) {
            setMessage('Please fill in all required fields');
            return;
        }

        try {
            const transactionData = {
                portfolioId: parseInt(portfolioId),
                stockId: parseInt(stockId),
                quantity: parseInt(quantity),
                pricePerShare: parseFloat(price),
                fees: parseFloat(fees) || 0,
            };

            if (transactionType === 'BUY') {
                await createBuyTransaction(transactionData);
            } else {
                await createSellTransaction(transactionData);
            }

            setMessage(`${transactionType} transaction created successfully!`);

            // Reset form
            setPortfolioId('');
            setStockId('');
            setQuantity('');
            setPrice('');
            setFees('0');
        } catch (error) {
            console.error('Error creating transaction:', error);
            setMessage('Error creating transaction');
        }
    };

    return (
        <Container sx={{ mt: 4 }}>
            <Typography variant="h4" gutterBottom>
                Add Transaction
            </Typography>

            <Paper sx={{ p: 3, maxWidth: 600 }}>
                {message && (
                    <Alert severity={message.includes('Error') ? 'error' : 'success'} sx={{ mb: 2 }}>
                        {message}
                    </Alert>
                )}

                <form onSubmit={handleSubmit}>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>

                        <TextField
                            select
                            label="Portfolio"
                            value={portfolioId}
                            onChange={(e) => setPortfolioId(e.target.value)}
                            required
                        >
                            {portfolios.map((portfolio) => (
                                <MenuItem key={portfolio.portfolioID} value={portfolio.portfolioID}>
                                    {portfolio.name}
                                </MenuItem>
                            ))}
                        </TextField>

                        <TextField
                            select
                            label="Transaction Type"
                            value={transactionType}
                            onChange={(e) => setTransactionType(e.target.value)}
                        >
                            <MenuItem value="BUY">Buy</MenuItem>
                            <MenuItem value="SELL">Sell</MenuItem>
                        </TextField>

                        <TextField
                            select
                            label="Stock"
                            value={stockId}
                            onChange={(e) => handleStockChange(e.target.value)}
                            required
                        >
                            {stocks.map((stock) => (
                                <MenuItem key={stock.stockID} value={stock.stockID}>
                                    {stock.symbol} - {stock.companyName}
                                </MenuItem>
                            ))}
                        </TextField>

                        <TextField
                            type="number"
                            label="Quantity"
                            value={quantity}
                            onChange={(e) => setQuantity(e.target.value)}
                            required
                        />

                        <TextField
                            type="number"
                            label="Price per Share"
                            value={price}
                            onChange={(e) => setPrice(e.target.value)}
                            required
                            inputProps={{ step: 0.01 }}
                        />

                        <TextField
                            type="number"
                            label="Fees"
                            value={fees}
                            onChange={(e) => setFees(e.target.value)}
                            inputProps={{ step: 0.01 }}
                        />

                        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                            <Button
                                type="button"
                                variant="outlined"
                                onClick={() => {
                                    setPortfolioId('');
                                    setStockId('');
                                    setQuantity('');
                                    setPrice('');
                                    setFees('0');
                                    setMessage('');
                                }}
                            >
                                Reset
                            </Button>
                            <Button type="submit" variant="contained">
                                Create {transactionType} Transaction
                            </Button>
                        </Box>
                    </Box>
                </form>
            </Paper>
        </Container>
    );
};

export default AddTransaction;