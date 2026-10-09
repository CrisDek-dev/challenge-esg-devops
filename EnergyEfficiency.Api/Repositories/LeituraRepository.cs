using EnergyEfficiency.Api.Data;
using EnergyEfficiency.Api.Models;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Repositories;

public class LeituraRepository : ILeituraRepository
{
    private readonly AppDbContext _db;

    public LeituraRepository(AppDbContext db) => _db = db;

    public async Task<LeituraConsumo> AddAsync(LeituraConsumo leitura, CancellationToken ct = default)
    {
        _db.Leituras.Add(leitura);
        await _db.SaveChangesAsync(ct);
        return leitura;
    }

    public IQueryable<LeituraConsumo> Query() =>
        _db.Leituras
            .Include(l => l.Equipamento)!
            .ThenInclude(e => e!.Setor)
            .AsNoTracking();

    public Task<decimal> SomaConsumoSetorPeriodoAsync(int setorId, DateTime inicio, DateTime fim, CancellationToken ct = default) =>
        _db.Leituras
            .Where(l => l.Equipamento!.SetorId == setorId && l.DataHora >= inicio && l.DataHora <= fim)
            .SumAsync(l => (decimal?)l.ConsumoKwh, ct)
            .ContinueWith(t => t.Result ?? 0m, ct);
}
