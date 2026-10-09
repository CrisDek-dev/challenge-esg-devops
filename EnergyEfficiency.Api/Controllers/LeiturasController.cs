using EnergyEfficiency.Api.Services;
using EnergyEfficiency.Api.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace EnergyEfficiency.Api.Controllers;

[ApiController]
[Route("api/[controller]")]
public class LeiturasController : ControllerBase
{
    private readonly ILeituraService _service;

    public LeiturasController(ILeituraService service) => _service = service;

    /// <summary>
    /// Lista leituras de consumo com paginacao e filtros.
    /// </summary>
    [HttpGet]
    [ProducesResponseType(typeof(PagedResult<LeituraResponse>), StatusCodes.Status200OK)]
    public async Task<ActionResult<PagedResult<LeituraResponse>>> Listar([FromQuery] LeituraFilter filtro, CancellationToken ct)
    {
        var result = await _service.ListarAsync(filtro, ct);
        return Ok(result);
    }

    /// <summary>
    /// Registra uma leitura enviada pelo sensor IoT. Gera alerta automaticamente
    /// se o consumo acumulado do mes exceder o limite do setor.
    /// </summary>
    [HttpPost]
    [Authorize]
    [ProducesResponseType(typeof(LeituraResponse), StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<ActionResult<LeituraResponse>> Registrar([FromBody] LeituraCreateRequest request, CancellationToken ct)
    {
        var leitura = await _service.RegistrarAsync(request, ct);
        return CreatedAtAction(nameof(Listar), new { id = leitura.Id }, leitura);
    }
}
