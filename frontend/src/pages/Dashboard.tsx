import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService, accountService, transactionService, categoryService } from '../api/client';
import { LogOut, Plus, ArrowUpRight, ArrowDownRight, Wallet, X } from 'lucide-react';

export const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [balance, setBalance] = useState(0);
  const [transactions, setTransactions] = useState<any[]>([]);
  const [accounts, setAccounts] = useState<any[]>([]);
  const [categories, setCategories] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalType, setModalType] = useState<'INCOME' | 'EXPENSE'>('INCOME');
  const [formData, setFormData] = useState({
    accountId: '',
    categoryId: '',
    description: '',
    amount: '',
    transactionDate: new Date().toISOString().split('T')[0]
  });
  const [modalLoading, setModalLoading] = useState(false);
  const [modalError, setModalError] = useState('');

  useEffect(() => {
    if (!authService.isAuthenticated()) {
      navigate('/login');
      return;
    }
    loadData();
  }, [navigate]);

  const loadData = async () => {
    try {
      const now = new Date();
      const firstDay = new Date(now.getFullYear(), now.getMonth(), 1).toISOString().split('T')[0];
      const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString().split('T')[0];

      const [accountsRes, transactionsRes, categoriesRes] = await Promise.all([
        accountService.getAccounts(),
        transactionService.getTransactions(firstDay, lastDay),
        categoryService.getCategories()
      ]);
      
      const totalBalance = accountsRes.reduce((acc: number, account: any) => acc + account.balance, 0);
      setBalance(totalBalance);
      
      setAccounts(accountsRes);
      setCategories(categoriesRes);
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

  const openModal = (type: 'INCOME' | 'EXPENSE') => {
    setModalType(type);
    setFormData({
      accountId: accounts.length > 0 ? accounts[0].id.toString() : '',
      categoryId: '',
      description: '',
      amount: '',
      transactionDate: new Date().toISOString().split('T')[0]
    });
    setModalError('');
    setIsModalOpen(true);
  };

  const handleCreateTransaction = async (e: React.FormEvent) => {
    e.preventDefault();
    setModalError('');
    setModalLoading(true);

    try {
      await transactionService.createTransaction({
        ...formData,
        type: modalType
      });
      setIsModalOpen(false);
      // Reload data to reflect new balance and transactions
      loadData();
    } catch (err: any) {
      const data = err.response?.data;
      let apiError = 'Erro ao criar transação.';
      if (data) {
        if (data.fields) {
          apiError = Object.values(data.fields).join(', ') || data.error;
        } else if (data.message) {
          apiError = data.message;
        }
      }
      setModalError(apiError);
    } finally {
      setModalLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-loading animate-fade-in">
        <div className="pulse-loader"></div>
      </div>
    );
  }

  // Filter categories by modal type
  const filteredCategories = categories.filter(c => c.type === modalType);

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
            <button className="action-btn glass-panel income" onClick={() => openModal('INCOME')}>
              <div className="icon-wrapper">
                <ArrowUpRight size={24} />
              </div>
              <span>Nova Receita</span>
              <Plus size={16} className="plus-icon" />
            </button>

            <button className="action-btn glass-panel expense" onClick={() => openModal('EXPENSE')}>
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
                        {new Date(t.date || t.transactionDate).toLocaleDateString('pt-BR')}
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

      {/* Transaction Modal */}
      {isModalOpen && (
        <div className="modal-overlay animate-fade-in">
          <div className="modal-content">
            <div className="modal-header">
              <h2>{modalType === 'INCOME' ? 'Nova Receita' : 'Nova Despesa'}</h2>
              <button onClick={() => setIsModalOpen(false)} className="modal-close">
                <X size={24} />
              </button>
            </div>
            
            {modalError && <div className="error-message" style={{marginBottom: '1rem'}}>{modalError}</div>}
            
            <form onSubmit={handleCreateTransaction}>
              <div className="input-group">
                <label>Conta</label>
                <select 
                  className="select-field"
                  value={formData.accountId}
                  onChange={(e) => setFormData({...formData, accountId: e.target.value})}
                  required
                >
                  <option value="" disabled>Selecione uma conta</option>
                  {accounts.map(acc => (
                    <option key={acc.id} value={acc.id}>{acc.name} - {formatCurrency(acc.balance)}</option>
                  ))}
                </select>
              </div>

              <div className="input-group">
                <label>Categoria</label>
                <select 
                  className="select-field"
                  value={formData.categoryId}
                  onChange={(e) => setFormData({...formData, categoryId: e.target.value})}
                  required
                >
                  <option value="" disabled>Selecione uma categoria</option>
                  {filteredCategories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>

              <div className="input-group">
                <label>Valor</label>
                <input 
                  type="number" 
                  step="0.01" 
                  min="0.01"
                  className="input-field" 
                  placeholder="0,00"
                  value={formData.amount}
                  onChange={(e) => setFormData({...formData, amount: e.target.value})}
                  required
                />
              </div>

              <div className="input-group">
                <label>Descrição</label>
                <input 
                  type="text" 
                  className="input-field" 
                  placeholder="Ex: Salário, Aluguel, etc."
                  value={formData.description}
                  onChange={(e) => setFormData({...formData, description: e.target.value})}
                  required
                />
              </div>

              <div className="input-group">
                <label>Data</label>
                <input 
                  type="date" 
                  className="input-field" 
                  value={formData.transactionDate}
                  onChange={(e) => setFormData({...formData, transactionDate: e.target.value})}
                  required
                />
              </div>

              <button type="submit" className="btn btn-primary full-width" disabled={modalLoading || accounts.length === 0}>
                {modalLoading ? 'Salvando...' : 'Salvar Transação'}
              </button>
              {accounts.length === 0 && (
                <p style={{color: 'var(--danger)', fontSize: '0.85rem', marginTop: '0.5rem', textAlign: 'center'}}>
                  Você precisa criar uma conta primeiro.
                </p>
              )}
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
