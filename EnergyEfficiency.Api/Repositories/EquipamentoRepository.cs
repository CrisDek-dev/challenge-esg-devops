using EnergyEfficiency.Api.Data;
using EnergyEfficiency.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Repositories;

public class EquipamentoRepository : IEquipamentoRepository
{
    private readonly AppDbContext _db;

    public EquipamentoRepository(AppDbContext db) => _db = db;

    public Task<Equipamento?> GetByIdAsync(int id, CancellationToken ct = default) =>
        _db.Equipamentos.Include(e => e.Setor).FirstOrDefaultAsync(e => e.Id == id, ct);

    public async Task UpdateAsync(Equipamento equipamento, CancellationToken ct = default)
    {
        _db.Equipamentos.Update(equipamento);
        await _db.SaveChangesAsync(ct);
    }

    public IQueryable<Equipamento> Query() =>
        _db.Equipamentos.Include(e => e.Setor).AsNoTracking();
}
