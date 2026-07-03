import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../api/client';
import { LogIn, User, Lock } from 'lucide-react';

export const Login: React.FC = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      await authService.login({ email, password });
      navigate('/dashboard');
    } catch (err: any) {
      const data = err.response?.data;
      let apiError = 'Credenciais inválidas ou erro no servidor.';
      
      if (data) {
        if (data.fields) {
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
            <LogIn size={32} className="text-primary" />
          </div>
          <h1 className="gradient-text">Bem-vindo de volta</h1>
          <p className="subtitle">Acesse seu painel financeiro</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="input-group">
            <label htmlFor="email">E-mail</label>
            <div className="input-wrapper">
              <User size={20} className="input-icon" />
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
              />
            </div>
          </div>

          <button type="submit" className="btn btn-primary full-width" disabled={loading}>
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <div className="auth-footer">
          <p>Ainda não tem uma conta? <Link to="/register" className="text-link">Cadastre-se</Link></p>
        </div>
      </div>
    </div>
  );
};
