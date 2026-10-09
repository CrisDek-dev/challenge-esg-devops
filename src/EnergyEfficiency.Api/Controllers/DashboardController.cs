using EnergyEfficiency.Api.Services;
using EnergyEfficiency.Api.ViewModels;
using Microsoft.AspNetCore.Mvc;

namespace EnergyEfficiency.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class DashboardController : ControllerBase
{
    private readonly IDashboardService _service;

    public DashboardController(IDashboardService service) => _service = service;

    /// <summary>
    /// Retorna o consumo agregado por setor no periodo, incluindo percentual do
    /// limite mensal utilizado e alertas em aberto.
    /// </summary>
    [HttpGet("consumo-por-setor")]
    [ProducesResponseType(typeof(IReadOnlyList<ConsumoSetorResponse>), StatusCodes.Status200OK)]
    public async Task<ActionResult<IReadOnlyList<ConsumoSetorResponse>>> ConsumoPorSetor(
        [FromQuery] RelatorioConsumoFilter filtro, CancellationToken ct)
    {
        var result = await _service.ConsumoPorSetorAsync(filtro, ct);
        return Ok(result);
    }
}
