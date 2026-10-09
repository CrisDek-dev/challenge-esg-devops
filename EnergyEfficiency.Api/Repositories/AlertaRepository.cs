using EnergyEfficiency.Api.Data;
using EnergyEfficiency.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Repositories;

public class AlertaRepository : IAlertaRepository
{
    private readonly AppDbContext _db;

    public AlertaRepository(AppDbContext db) => _db = db;

    public async Task<AlertaConsumo> AddAsync(AlertaConsumo alerta, CancellationToken ct = default)
    {
        _db.Alertas.Add(alerta);
        await _db.SaveChangesAsync(ct);
        return alerta;
    }

    public IQueryable<AlertaConsumo> Query() =>
        _db.Alertas.Include(a => a.Setor).AsNoTracking();

    public Task<AlertaConsumo?> GetByIdAsync(long id, CancellationToken ct = default) =>
        _db.Alertas.Include(a => a.Setor).FirstOrDefaultAsync(a => a.Id == id, ct);

    public async Task UpdateAsync(AlertaConsumo alerta, CancellationToken ct = default)
    {
        _db.Alertas.Update(alerta);
        await _db.SaveChangesAsync(ct);
    }

    public Task<bool> ExistsAbertoForSetorAsync(int setorId, CancellationToken ct = default) =>
        _db.Alertas.AnyAsync(a => a.SetorId == setorId && !a.Resolvido, ct);
}
