using EnergyEfficiency.Api.Data;
using EnergyEfficiency.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Repositories;

public class UsuarioRepository : IUsuarioRepository
{
    private readonly AppDbContext _db;

    public UsuarioRepository(AppDbContext db) => _db = db;

    public Task<Usuario?> GetByEmailAsync(string email, CancellationToken ct = default) =>
        _db.Usuarios.FirstOrDefaultAsync(u => u.Email == email, ct);
}
