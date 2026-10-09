using EnergyEfficiency.Api.Exceptions;
using EnergyEfficiency.Api.Repositories;
using EnergyEfficiency.Api.Security;
using EnergyEfficiency.Api.ViewModels;

namespace EnergyEfficiency.Api.Services;

public class AuthService : IAuthService
{
    private readonly IUsuarioRepository _usuarios;
    private readonly IJwtTokenGenerator _jwt;

    public AuthService(IUsuarioRepository usuarios, IJwtTokenGenerator jwt)
    {
        _usuarios = usuarios;
        _jwt = jwt;
    }

    public async Task<LoginResponse> LoginAsync(LoginRequest request, CancellationToken ct = default)
    {
        var user = await _usuarios.GetByEmailAsync(request.Email, ct);
        if (user is null || !BCrypt.Net.BCrypt.Verify(request.Senha, user.SenhaHash))
        {
            throw new BusinessException("Email ou senha invalidos.");
        }

        var (token, expira) = _jwt.GenerateToken(user);
        return new LoginResponse
        {
            Token = token,
            ExpiraEm = expira,
            Nome = user.Nome,
            Papel = user.Papel
        };
    }
}
