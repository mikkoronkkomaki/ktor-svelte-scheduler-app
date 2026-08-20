<script lang="ts">
  import {onMount} from 'svelte'
  import {appointmentsApi, type Appointment} from '$lib/api'

  let items = $state<Appointment[]>([])
  let loading = $state(true)
  let error = $state('')
  let searchTerm = $state('')

  $effect(() => {
    // Filtteröity lista hakutermin perusteella
  })

  async function loadAppointments() {
    loading = true
    error = ''
    try {
      items = await appointmentsApi.list()
    } catch (e) {
      error = e instanceof Error ? e.message : 'Tuntematon virhe'
    } finally {
      loading = false
    }
  }

  function getFilteredItems() {
    if (!searchTerm.trim()) return items
    const term = searchTerm.toLowerCase()
    return items.filter(
      (item) =>
        item.description.toLowerCase().includes(term) ||
        item.status.toLowerCase().includes(term)
    )
  }

  onMount(loadAppointments)
</script>

<section class="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
    <h2 class="mb-4 text-lg font-semibold text-slate-900">Varausten haku</h2>

    {#if error}
        <div class="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
        </div>
    {/if}

    {#if loading}
        <p class="text-slate-600">Ladataan...</p>
    {:else}
        <div class="mb-4">
            <input
                    type="text"
                    bind:value={searchTerm}
                    placeholder="Hae kuvauksella tai statuksella..."
                    class="w-full rounded-lg border border-slate-300 px-3 py-2 text-slate-900 outline-none ring-indigo-200 transition focus:border-indigo-500 focus:ring"
            />
        </div>

        {#if getFilteredItems().length === 0}
            <p class="text-slate-600">Ei hakutuloksia.</p>
        {:else}
            <div class="overflow-x-auto">
                <table class="w-full border-collapse text-sm">
                    <thead>
                    <tr class="border-b border-slate-200 bg-slate-50">
                        <th class="px-4 py-2 text-left font-semibold text-slate-700">Kuvaus</th>
                        <th class="px-4 py-2 text-left font-semibold text-slate-700">Asiakas</th>
                        <th class="px-4 py-2 text-left font-semibold text-slate-700">Asiantuntija</th>
                        <th class="px-4 py-2 text-left font-semibold text-slate-700">Alkuaika</th>
                        <th class="px-4 py-2 text-left font-semibold text-slate-700">Loppuaika</th>
                        <th class="px-4 py-2 text-left font-semibold text-slate-700">Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    {#each getFilteredItems() as item}
                        <tr class="border-b border-slate-200 hover:bg-slate-50">
                            <td class="px-4 py-2 text-slate-900">{item.description}</td>
                            <td class="px-4 py-2 text-slate-600">
                                {#if item.client}
                                    {item.client.firstName} {item.client.lastName}
                                {:else}
                                    -
                                {/if}
                            </td>
                            <td class="px-4 py-2 text-slate-600">
                                {#if item.specialist}
                                    {item.specialist.firstName} {item.specialist.lastName}
                                {:else}
                                    -
                                {/if}
                            </td>
                            <td class="px-4 py-2 text-slate-600">{item.startTime}</td>
                            <td class="px-4 py-2 text-slate-600">{item.endTime}</td>
                            <td class="px-4 py-2">
							<span class="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-medium text-slate-700">
								{item.status}
							</span>
                            </td>
                        </tr>
                    {/each}
                    </tbody>
                </table>
            </div>
        {/if}
    {/if}
</section>