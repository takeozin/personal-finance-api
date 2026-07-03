import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService, accountService, transactionService } from '../api/client';
import { LogOut, Plus, ArrowUpRight, ArrowDownRight, Wallet } from 'lucide-react';

export const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [balance, setBalance] = useState(0);
  const [transactions, setTransactions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!authService.isAuthenticated()) {
      navigate('/login');
      return;
    }
    loadData();
  }, [navigate]);

  const loadData = async () => {
    try {
      const [accountsRes, transactionsRes] = await Promise.all([
        accountService.getAccounts(),
        transactionService.getTransactions()
      ]);
      
      // Calculate total balance from all accounts
      const totalBalance = accountsRes.reduce((acc: number, account: any) => acc + account.balance, 0);
      setBalance(totalBalance);
      
      setTransactions(transactionsRes);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  if (loading) {
    return (
      <div className="dashboard-loading animate-fade-in">
        <div className="pulse-loader"></div>
      </div>
    );
  }

  return (
    <div className="app-container">
      <header className="dashboard-header glass-panel">
        <div className="header-content">
          <div className="logo">
            <Wallet size={28} className="text-primary" />
            <span className="font-bold">Personal Finance</span>
          </div>
          <button onClick={handleLogout} className="btn-icon" title="Sair">
            <LogOut size={20} />
          </button>
        </div>
      </header>

      <main className="main-content">
        <div className="dashboard-top animate-fade-in">
          <div className="balance-card glass-panel">
            <h3>Saldo Atual</h3>
            <h1 className="gradient-text">{formatCurrency(balance)}</h1>
          </div>

          <div className="action-cards">
            <button className="action-btn glass-panel income">
              <div className="icon-wrapper">
                <ArrowUpRight size={24} />
              </div>
              <span>Nova Receita</span>
              <Plus size={16} className="plus-icon" />
            </button>

            <button className="action-btn glass-panel expense">
              <div className="icon-wrapper">
                <ArrowDownRight size={24} />
              </div>
              <span>Nova Despesa</span>
              <Plus size={16} className="plus-icon" />
            </button>
          </div>
        </div>

        <div className="transactions-section glass-panel animate-fade-in-delayed">
          <div className="section-header">
            <h2>Transações Recentes</h2>
            <button className="btn btn-secondary text-sm">Ver todas</button>
          </div>

          {transactions.length === 0 ? (
            <div className="empty-state">
              <p>Nenhuma transação encontrada.</p>
              <p className="subtitle">Comece adicionando uma nova receita ou despesa.</p>
            </div>
          ) : (
            <ul className="transaction-list">
              {transactions.slice(0, 5).map((t) => (
                <li key={t.id} className="transaction-item">
                  <div className="transaction-info">
                    <div className={`transaction-icon ${t.type === 'INCOME' ? 'income' : 'expense'}`}>
                      {t.type === 'INCOME' ? <ArrowUpRight size={20} /> : <ArrowDownRight size={20} />}
                    </div>
                    <div>
                      <p className="transaction-desc">{t.description}</p>
                      <p className="transaction-date">
                        {new Date(t.date).toLocaleDateString('pt-BR')}
                      </p>
                    </div>
                  </div>
                  <div className={`transaction-amount ${t.type === 'INCOME' ? 'text-success' : 'text-danger'}`}>
                    {t.type === 'INCOME' ? '+' : '-'}{formatCurrency(t.amount)}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </main>
    </div>
  );
};
