import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../api/client';
import { UserPlus, User, Lock, Mail } from 'lucide-react';

export const Register: React.FC = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      await authService.register({ name, email, password });
      navigate('/dashboard');
    } catch (err: any) {
      const data = err.response?.data;
      let apiError = 'Erro ao criar conta. Verifique seus dados.';
      
      if (data) {
        if (data.fields) {
          // Extrair mensagens de validação
          const fieldErrors = Object.values(data.fields).join(', ');
          apiError = fieldErrors || data.error;
        } else if (data.message) {
          apiError = data.message;
        }
      }
      
      setError(apiError);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="glass-panel auth-panel animate-fade-in">
        <div className="auth-header">
          <div className="auth-icon-wrapper">
            <UserPlus size={32} className="text-secondary" />
          </div>
          <h1 className="gradient-text">Criar Conta</h1>
          <p className="subtitle">Comece a controlar suas finanças</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="input-group">
            <label htmlFor="name">Nome completo</label>
            <div className="input-wrapper">
              <User size={20} className="input-icon" />
              <input
                id="name"
                type="text"
                className="input-field with-icon"
                placeholder="Seu nome"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="input-group">
            <label htmlFor="email">E-mail</label>
            <div className="input-wrapper">
              <Mail size={20} className="input-icon" />
              <input
                id="email"
                type="email"
                className="input-field with-icon"
                placeholder="seu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="input-group">
            <label htmlFor="password">Senha</label>
            <div className="input-wrapper">
              <Lock size={20} className="input-icon" />
              <input
                id="password"
                type="password"
                className="input-field with-icon"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                minLength={6}
              />
            </div>
          </div>

          <button type="submit" className="btn btn-primary full-width" disabled={loading}>
            {loading ? 'Criando conta...' : 'Cadastrar'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Já tem uma conta? <Link to="/login" className="text-link">Faça Login</Link></p>
        </div>
      </div>
    </div>
  );
};
