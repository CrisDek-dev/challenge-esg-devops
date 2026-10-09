using EnergyEfficiency.Api.ViewModels;
using Microsoft.EntityFrameworkCore;

namespace EnergyEfficiency.Api.Helpers;

public static class PagingExtensions
{
    public static async Task<PagedResult<T>> ToPagedResultAsync<T>(
        this IQueryable<T> source,
        int page,
        int pageSize,
        CancellationToken ct = default)
    {
        page = page < 1 ? 1 : page;
        pageSize = pageSize switch
        {
            < 1 => 10,
            > 100 => 100,
            _ => pageSize
        };

        var total = await source.LongCountAsync(ct);
        var items = await source.Skip((page - 1) * pageSize).Take(pageSize).ToListAsync(ct);

        return new PagedResult<T>
        {
            Items = items,
            Page = page,
            PageSize = pageSize,
            TotalItems = total
        };
    }
}
