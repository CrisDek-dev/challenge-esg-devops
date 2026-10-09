using EnergyEfficiency.Api.Data;
using EnergyEfficiency.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Repositories;

public class SetorRepository : ISetorRepository
{
    private readonly AppDbContext _db;

    public SetorRepository(AppDbContext db) => _db = db;

    public Task<Setor?> GetByIdAsync(int id, CancellationToken ct = default) =>
        _db.Setores.FirstOrDefaultAsync(s => s.Id == id, ct);

    public IQueryable<Setor> Query() =>
        _db.Setores.AsNoTracking();
}
